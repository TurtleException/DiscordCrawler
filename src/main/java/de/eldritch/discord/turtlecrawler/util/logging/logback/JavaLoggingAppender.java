package de.eldritch.discord.turtlecrawler.util.logging.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import de.eldritch.discord.turtlecrawler.jda.JDAWrapper;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class JavaLoggingAppender extends AppenderBase<ILoggingEvent> {
    public static Level jdaLevelFilter = Level.INFO;

    @Override
    protected void append(ILoggingEvent event) {
        LogRecord record = new LogRecord(Level.INFO, event.getFormattedMessage());

        // set level
        if      (event.getLevel().equals(ch.qos.logback.classic.Level.ALL))     record.setLevel(Level.ALL);
        else if (event.getLevel().equals(ch.qos.logback.classic.Level.DEBUG))   record.setLevel(Level.FINER);
        else if (event.getLevel().equals(ch.qos.logback.classic.Level.ERROR))   record.setLevel(Level.SEVERE);
        else if (event.getLevel().equals(ch.qos.logback.classic.Level.INFO))    record.setLevel(Level.INFO);
        else if (event.getLevel().equals(ch.qos.logback.classic.Level.OFF))     record.setLevel(Level.OFF);
        else if (event.getLevel().equals(ch.qos.logback.classic.Level.TRACE))   record.setLevel(Level.FINE);
        else if (event.getLevel().equals(ch.qos.logback.classic.Level.WARN))    record.setLevel(Level.WARNING);

        record.setInstant(Instant.ofEpochMilli(event.getTimeStamp()));

        if (event.getThrowableProxy() != null && event.getThrowableProxy() instanceof ThrowableProxy t)
            record.setThrown(t.getThrowable());

        record.setLoggerName("JDA-internal");


        // log
        if (record.getLevel().intValue() >= jdaLevelFilter.intValue())
            JDAWrapper.LOGGER_INTERNAL.log(record);
    }
}