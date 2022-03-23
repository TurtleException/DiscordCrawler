package de.eldritch.discord.turtlecrawler.task;

import de.eldritch.discord.turtlecrawler.util.logging.NestedToggleLogger;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a task that can be registered at a {@link TaskManager}. Tasks should generally be executed by the
 * {@link TaskExecutor} but can also be called manually when they should block the calling {@link Thread}.
 */
public abstract class Task implements Runnable {
    /**
     * Logger that reports to the Logger of the {@link TaskManager}
     */
    protected final NestedToggleLogger logger;
    /**
     * {@link TaskManager} responsible for this task.
     * If the Task is not executed by the {@link TaskExecutor} the TaskManager of the super-task can be provided.
     */
    protected final TaskManager manager;
    /**
     * Name of the task - mainly used for the logger.
     */
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
