package de.eldritch.discord.turtlecrawler.task;

import de.eldritch.discord.turtlecrawler.util.logging.NestedToggleLogger;
import org.jetbrains.annotations.NotNull;

public abstract class Task implements Runnable {
    protected final NestedToggleLogger logger;
    protected final TaskManager manager;
    private final String name;

    protected Task(@NotNull TaskManager manager, @NotNull String name) {
        this.name = "TASK/" + name;

        this.manager = manager;
        logger = new NestedToggleLogger(name, manager.getLogger());
    }

    /**
     * Provides the {@link NestedToggleLogger Logger} instance of this task.
     * @return The tasks' logger
     */
    public final @NotNull NestedToggleLogger getLogger() {
        return logger;
    }

    public final @NotNull String getName() {
        return name;
    }
}
