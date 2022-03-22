package de.eldritch.discord.turtlecrawler.task;

import de.eldritch.discord.turtlecrawler.task.tasks.ChannelTask;

public class TaskView {
    private Metadata metadata;

    public ChannelTask toTask() throws IllegalStateException {
        throw new IllegalStateException("Unable to build task from non-active TaskView");
    }
}
