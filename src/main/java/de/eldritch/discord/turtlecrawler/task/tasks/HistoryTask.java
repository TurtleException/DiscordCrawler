package de.eldritch.discord.turtlecrawler.task.tasks;

import de.eldritch.discord.turtlecrawler.task.Task;
import de.eldritch.discord.turtlecrawler.task.TaskManager;
import de.eldritch.discord.turtlecrawler.util.RestActionUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
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

public class HistoryTask extends Task {
    private final MessageChannel channel;
    private final String message;
    private final int limit;

    private final TreeSet<DataObject> data;

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

                manager.register(new AttachmentTask(manager, channel, attachment));
            }

            logger.log(Level.FINER, "Processed " + attachments.length() + " attachments.");
        }

        logger.log(Level.FINE, "Checked " + array.length() + " messages for attachments.");
    }

    public List<DataObject> getData() {
        return List.copyOf(data);
    }

    public boolean isShortened() {
        return limit > data.size();
    }
}
