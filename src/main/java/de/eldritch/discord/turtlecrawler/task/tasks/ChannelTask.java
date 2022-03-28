package de.eldritch.discord.turtlecrawler.task.tasks;

import de.eldritch.discord.turtlecrawler.task.Task;
import de.eldritch.discord.turtlecrawler.task.TaskManager;
import de.eldritch.discord.turtlecrawler.util.MiscUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * A Task that progressively retrieves the history of a {@link MessageChannel} and saves messages to a file.
 */
public class ChannelTask extends Task {
    private final MessageChannel channel;

    /**
     * File with the metadata of the channel.
     */
    private final File fileMetadata;
    /**
     * File with the message history of the channel.
     */
    private final File fileMessages;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ChannelTask(@NotNull TaskManager manager, @NotNull MessageChannel channel) throws IOException {
        super(manager, "CHANNEL/" + provideName(channel));
        this.channel = channel;


        File dir = new File(manager.getDIR(), MiscUtil.getDIR(channel));
        dir.mkdirs();

        this.fileMetadata = new File(dir, "metadata.json");
        this.fileMetadata.createNewFile();

        this.fileMessages = new File(dir, "messages.json");
        this.fileMessages.createNewFile();
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Starting task (Channel \"" + channel.getName() + "\")");

        /* ----- METADATA ----- */

        logger.log(Level.FINE, "Processing metadata.");

        Route.CompiledRoute route = Route.Channels.GET_CHANNEL.compile(channel.getId());
        DataObject data = new RestActionImpl<DataObject>(channel.getJDA(), route, (response, request) -> response.getObject()).priority().complete();

        try {
            FileWriter writer = new FileWriter(fileMetadata, false);

            writer.append(data.toPrettyString());

            writer.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Encountered an unexpected exception while writing metadata file.", e);
        }


        /* ----- MESSAGES ----- */

        // incremented with every message that is being processed
        int amount = 0;

        HistoryTask historyTask = null;
        String messageId = "0";

        // buffers new lines as new history tasks are being executed
        LinkedList<String> lineBuffer = new LinkedList<>();

        while (historyTask == null || !historyTask.isShortened()) {
            logger.log(Level.FINER, "Attempting to query 100 messages...");
            historyTask = new HistoryTask(manager, channel, messageId, 100);

            historyTask.run();

            List<DataObject> messages = historyTask.getData();

            logger.log(Level.FINER, "Retrieved " + messages.size() + " response objects.");

            for (DataObject message : messages) {
                messageId = message.getString("id", null);
                lineBuffer.add(message.toPrettyString());
                amount++;
            }

            // save messages
            logger.log(Level.FINER, "Writing to file...");
            try {
                this.write(lineBuffer);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Encountered an unexpected exception while writing message file.", e);
            }
        }
        logger.log(Level.FINE, "No more history to process.");
        logger.log(Level.INFO, "Task finished with " + amount + " processed messages. (Channel \"" + channel.getName() + "\")");
    }

    /**
     * Writes buffered lines provided in a {@link LinkedList} to the message file.
     * @param lines Buffered lines.
     * @throws IOException if an issue with the filesystem occurs.
     */
    private void write(@NotNull LinkedList<String> lines) throws IOException {
        FileWriter writer = new FileWriter(fileMessages, true);
        while (!lines.isEmpty())
            writer.append(lines.removeFirst()).append("\n");
        writer.close();
    }

    /**
     * Provides the channel part of the name of a {@link ChannelTask}. If the Channel is an instance of
     * {@link BaseGuildMessageChannel} the snowflake id of the {@link Guild} is added as prefix.
     * <p> The provided name represents the part after <code>CHANNEL/</code>.
     * @param channel Channel of this Task
     * @return String representation of the name.
     */
    public static String provideName(MessageChannel channel) {
        return ((channel instanceof BaseGuildMessageChannel guildChannel) ? "G" + guildChannel.getGuild().getId() + "/C" : "P") + channel.getId();
    }
}
