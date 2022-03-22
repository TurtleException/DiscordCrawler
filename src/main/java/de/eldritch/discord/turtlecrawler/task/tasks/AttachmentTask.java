package de.eldritch.discord.turtlecrawler.task.tasks;

import de.eldritch.discord.turtlecrawler.task.Completable;
import de.eldritch.discord.turtlecrawler.task.Task;
import de.eldritch.discord.turtlecrawler.task.TaskManager;
import org.jetbrains.annotations.NotNull;

public class AttachmentTask extends Task implements Completable {
    protected AttachmentTask(@NotNull TaskManager manager, @NotNull String name) {
        super(manager, name);
    }

    @Override
    public void complete() {

    }

    @Override
    public void run() {

    }
}
