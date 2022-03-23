package de.eldritch.discord.turtlecrawler.task.tasks;

import de.eldritch.discord.turtlecrawler.task.Task;
import de.eldritch.discord.turtlecrawler.task.TaskManager;
import de.eldritch.discord.turtlecrawler.util.MiscUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/**
 * A Task that downloads an attachment file.
 */
public class AttachmentTask extends Task {
    private final Message.Attachment attachment;
    private final MessageChannel channel;

    public AttachmentTask(@NotNull TaskManager manager, @NotNull MessageChannel channel, @NotNull Message.Attachment attachment) {
        super(manager, "ATTACHMENT/" + ChannelTask.provideName(channel) + "/A" + attachment.getId());

        this.attachment = attachment;
        this.channel = channel;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void run() {
        logger.log(Level.FINE, "Preparing file download...");

        File file = new File(manager.getDIR(), MiscUtil.getDIR(channel)
                + File.separator + "attachments"
                + File.separator + attachment.getId()
                + "." + attachment.getFileExtension());

        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to create output file", e);
            return;
        }

        logger.log(Level.FINE, "Downloading file... ("  + attachment.getSize() + " bytes)");

        try {
            attachment.downloadToFile(file).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.WARNING, "Failed to download attachment " + attachment.getFileName(), e);
        }

        logger.log(Level.FINE, "File downloaded.");
    }
}
