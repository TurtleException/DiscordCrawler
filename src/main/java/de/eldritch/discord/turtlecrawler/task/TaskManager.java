package de.eldritch.discord.turtlecrawler.task;

import de.eldritch.discord.turtlecrawler.DiscordTurtleCrawler;
import de.eldritch.discord.turtlecrawler.DistributionManager;
import de.eldritch.discord.turtlecrawler.util.logging.NestedToggleLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.logging.Level;

/**
 * The TaskManager is responsible for keeping track of all active {@link Task Tasks} and functions as an interface
 * between tasks and the {@link TaskExecutor}. Different to Tasks and the TaskExecutor the TaskManager runs on the
 * main {@link Thread}.
 */
public class TaskManager {
    /**
     * Static logger
     * @see TaskManager#getLogger()
     */
    private final NestedToggleLogger logger;

    /**
     * The unique name of this manager provided by {@link DistributionManager#newTaskManager()}.
     */
    private final String name;
    /**
     * The designated directory for this manager.
     */
    private final File dir;

    /**
     * Set of all tasks this manager is responsible for.
     */
    private final HashSet<Task> tasks = new HashSet<>();

    /**
     * The total amount of processed messages by this TaskManager.
     */
    private long totalProcessedMessages = 0L;

    /**
     * ExecutorService responsible for submissions by tasks.
     */
    private final TaskExecutor executor;

    /**
     * Default constructor. This can only be called once as the program is not optimized for multiple TaskManagers.
     * Asynchronous managing of tasks would cause complexity, which can be avoided as {@link Task} objects and the
     * {@link TaskExecutor} are asynchronous themselves.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public TaskManager(@NotNull String name) {
        this.name = name;

        this.dir = new File(DistributionManager.OUTPUT_DIR, name);
        this.dir.mkdir();

        logger = new NestedToggleLogger("MANAGER#" + name, DiscordTurtleCrawler.LOGGER);

        executor = new TaskExecutor(this);
    }

    /**
     * Submits a {@link Task} to this manager. The task will be started and observed from now on.
     * @param task The task object.
     */
    public synchronized void register(@NotNull Task task) {
        if (tasks.contains(task))
            throw new IllegalArgumentException("Task is already registered");

        logger.log(Level.FINE, "Submitting " + task.getName() + "...");

        executor.submit(() -> {
            logger.log(Level.FINE, "Starting " + task.getName() + ".");
            task.run();
            logger.log(Level.FINE, task.getName() + " is done.");

            if (task instanceof Completable completable) {
                logger.log(Level.FINE, "Submitting " + task.getName() + " for completion...");
                executor.submit(() -> {
                    logger.log(Level.FINE, "Completing " + task.getName() + "...");
                    completable.complete();
                    logger.log(Level.FINE, task.getName() + " is done.");
                });
            }
        });
    }

    /**
     * Provides the output directory of this TaskManager.
     * @return Output directory.
     */
    public File getDIR() {
        return dir;
    }

    /**
     * Provides the {@link NestedToggleLogger} of this TaskManager.
     * @return "<code>MANAGER#[name]</code>" logger
     */
    NestedToggleLogger getLogger() {
        return logger;
    }

    /**
     * Provides the approximate amount of tasks that are currently managed by the Executor.
     * @return Number of scheduled tasks that have not finished execution.
     */
    public long getTaskAmount() {
        return executor.getTaskCount() - executor.getCompletedTaskCount();
    }

    public synchronized long updateMessages(long l) {
        return totalProcessedMessages += l;
    }

    public long getTotalProcessedMessages() {
        return updateMessages(0);
    }
}
