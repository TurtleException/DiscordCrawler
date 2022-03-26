package de.eldritch.discord.turtlecrawler.util.logging;

import de.eldritch.discord.turtlecrawler.util.Queue;

import java.util.Arrays;
import java.util.logging.*;

public final class SystemOutputToggleLogger extends Logger {
    private boolean active = true;

    private final Queue<LogRecord> queue = new Queue<>(100);

    private final SimpleFormatter simpleFormatter = new SimpleFormatter();

    public SystemOutputToggleLogger(String name) {
        super(name, null);

        this.setLevel(Level.INFO);
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
        if (this.getLevel().intValue() > record.getLevel().intValue()) return;

        record.setLoggerName(getName());
        System.out.print(simpleFormatter.format(record));
    }

    private void queue(LogRecord record) {
        queue.add(record);
    }

    public void shutdown() {
        Arrays.stream(getHandlers()).forEach(Handler::close);
    }
}
