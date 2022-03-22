package de.eldritch.discord.turtlecrawler.task.tasks;

import de.eldritch.discord.turtlecrawler.task.Completable;
import de.eldritch.discord.turtlecrawler.task.Task;
import de.eldritch.discord.turtlecrawler.task.TaskManager;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BulkMessageTask extends Task implements Completable {
    private final List<Message> messages;
    private final List<String> csvEntries;

    private final int id;

    private volatile boolean done = false;

    public BulkMessageTask(@NotNull TaskManager manager, @NotNull ChannelTask channelTask, int id, @NotNull List<Message> messages) {
        super(manager, channelTask.getName() + "#BULK_MESSAGE/" + id);

        this.id = id;
        this.messages = List.copyOf(messages);
        this.csvEntries = new ArrayList<>();
    }

    @Override
    public void run() {

    }

    @Override
    public void complete() {
        done = true;
    }

    public void join() {
        while (!done) {
            Thread.onSpinWait();
        }
    }
}
