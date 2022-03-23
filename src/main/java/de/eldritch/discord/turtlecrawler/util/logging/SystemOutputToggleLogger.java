package de.eldritch.discord.turtlecrawler.util.logging;

import de.eldritch.discord.turtlecrawler.util.Queue;

import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public final class SystemOutputToggleLogger extends Logger {
    private boolean active = true;

    private final Queue<LogRecord> queue = new Queue<>(100);

    private final StreamHandler systemOutHandler;

    public SystemOutputToggleLogger(String name) {
        super(name, null);

        systemOutHandler = new StreamHandler(System.out, new SimpleFormatter());
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean b) {
        this.active = b;

        if (b) {
            // publish queued records
            for (LogRecord record : queue) {
                publishToSystemOut(record);
            }
        }
    }

    @Override
    public void log(LogRecord record) {
        if (active)
            publishToSystemOut(record);
        else
            queue(record);

        super.log(record);
    }

    public void publishToSystemOut(LogRecord record) {
        systemOutHandler.publish(record);
    }

    private void queue(LogRecord record) {
        queue.add(record);
    }

    public void shutdown() {
        Arrays.stream(getHandlers()).forEach(Handler::close);
    }
}
