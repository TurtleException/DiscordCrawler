package de.eldritch.discord.turtlecrawler.task.tasks;

import de.eldritch.discord.turtlecrawler.task.Task;
import de.eldritch.discord.turtlecrawler.task.TaskManager;
import de.eldritch.discord.turtlecrawler.util.RestActionUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.Route;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;

/**
 * A task that iterates through up tp 100 messages and provides them as a {@link TreeSet} of
 * {@link DataObject DataObjects} sorted from oldest to newest.
 */
public class HistoryTask extends Task {
    private final MessageChannel channel;
    /**
     * Snowflake ID of the pivot message.
     */
    private final String message;
    /**
     * Maximum amount of messages to retrieve.
     * @see MessageHistory#getHistoryAfter(MessageChannel, String)
     */
    private final int limit;

    /**
     * Message buffer, sorted by id (oldest to newest)
     */
    private final TreeSet<DataObject> data;

    /**
     * Creates a new HistoryTask that pivots on the provided snowflake ID of a message and retrieves messages up to the
     * specified limit.
     * @param manager TaskManager responsible for this task.
     * @param channel Channel to retrieve history from.
     * @param message Snowflake ID of the pivot message, "<code>0</code>" if the pivot should be set to the beginning of
     *                the channel.
     * @param limit Maximum amount of messages to retrieve.
     * @see ChannelTask#run()
     */
    public HistoryTask(@NotNull TaskManager manager, @NotNull MessageChannel channel, @NotNull String message, @Range(from = 1, to = 100) int limit) {
        super(manager, "HISTORY/"
                + ChannelTask.provideName(channel) + "/M"
                + message + ">" + limit);

        this.channel = channel;
        this.message = message;
        this.limit = limit;

        // sort messages by id (from oldest to newest)
        this.data = new TreeSet<>(Comparator.comparingLong(dataObject -> dataObject.getLong("id")));
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Retrieving history for channel " + channel.getName());

        Route.CompiledRoute route = Route.Messages.GET_MESSAGE_HISTORY.compile(channel.getId()).withQueryParams("after", message, "limit", String.valueOf(limit));
        DataArray array = RestActionUtil.getDataArray(channel.getJDA(), route).complete();

        for (int i = 0; i < array.length(); i++) {
            try {
                data.add(array.getObject(i));
            } catch (Exception e) {
                logger.log(Level.WARNING, "Encountered an error in message pagination", e);
            }
        }

        manager.updateMessages(data.size());


        /* ----- PROCESS ATTACHMENTS ----- */
        logger.log(Level.FINE, "Iterating messages to find attachments...");

        EntityBuilder builder = new EntityBuilder(channel.getJDA());
        for (int i = 0; i < array.length(); i++) {
            logger.log(Level.FINER, "Checking message " + i + " of " + array.length() + ".");

            DataArray attachments = array.getObject(i).getArray("attachments");

            for (int j = 0; j < attachments.length(); j++) {
                Message.Attachment attachment = builder.createMessageAttachment(attachments.getObject(j));

                new AttachmentTask(manager, channel, attachment).run();
            }

            logger.log(Level.FINER, "Processed " + attachments.length() + " attachments.");
        }

        logger.log(Level.FINE, "Checked " + array.length() + " messages for attachments.");

        logger.log(Level.INFO, "Processed " + array.length() + " messages from channel " + channel.getName());
    }

    /**
     * Provides the message buffer containing {@link DataObject} representations of messages.
     * <p>This method will return an empty buffer if {@link HistoryTask#run()} has not been called yet, or it has been
     * called asynchronously and has not yet returned.
     * @return Message buffer.
     */
    public List<DataObject> getData() {
        return List.copyOf(data);
    }

    /**
     * Return <code>true</code> if the retrieved history is shorter than it could have been according to the provided
     * limit, indicating that the end of the channel has been reached.
     * @return <code>true</code> if the retrieved history is shorter than the limit.
     */
    public boolean isShortened() {
        return limit > data.size();
    }
}
