package de.eldritch.discord.turtlecrawler.util.logging;

import de.eldritch.discord.turtlecrawler.Config;
import de.eldritch.discord.turtlecrawler.util.Queue;
import org.jetbrains.annotations.NotNull;

import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * A very simple way to have an easier implementation of nested {@link Logger Loggers}. It simply disables use of parent
 * handlers and publishes {@link LogRecord LogRecords} manually after changing their message content to embed the name.
 * <p>This implementation can also be toggled. While the logger is toggled off all records are queued up to a maximum
 * capacity and will be published when the logger is toggled back on again (in correct order).
 */
@SuppressWarnings("unused")
public class NestedToggleLogger extends Logger {
    private boolean active = true;

    private final Queue<LogRecord> queue = new Queue<>(100);

    public NestedToggleLogger(@NotNull String name, @NotNull Logger parent) {
        super(name, null);

        this.setUseParentHandlers(false);

        // manually publish records to parent
        this.addHandler(new StreamHandler() {
            @Override
            public void publish(LogRecord record) {
                record.setMessage("[" + record.getLoggerName() + "] " + record.getMessage());
                parent.log(record);
            }
        });
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean b) {
        this.active = b;

        if (b) {
            // publish queued records
            for (LogRecord record : queue.toArray(new LogRecord[]{})) {
                this.log(record);
            }
        }
    }

    @Override
    public void log(LogRecord record) {
        if (active)
            super.log(record);
        else
            queue(record);
    }

    private void queue(LogRecord record) {
        queue.add(record);
    }
}
