package de.eldritch.discord.turtlecrawler.task;

import de.eldritch.discord.turtlecrawler.util.logging.NestedToggleLogger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * An {@link ExecutorService} that is responsible for executing {@link Task Tasks}.
 */
public class TaskExecutor extends ThreadPoolExecutor {
    private final NestedToggleLogger logger;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final TaskManager manager;

    TaskExecutor(@NotNull TaskManager manager) {
        super(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>());

        this.manager = manager;
        this.logger = new NestedToggleLogger("EXECUTOR", manager.getLogger());
    }



    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (t != null) {
            if (r instanceof Task task) {
                logger.log(Level.WARNING, task.getName() + " failed execution.", t);
            } else {
                logger.log(Level.WARNING, "Unknown Runnable failed execution.", t);
            }
        }

        manager.checkDone();

        super.afterExecute(r, t);
    }
}
