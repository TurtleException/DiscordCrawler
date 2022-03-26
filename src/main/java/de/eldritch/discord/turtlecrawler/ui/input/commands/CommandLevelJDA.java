package de.eldritch.discord.turtlecrawler.ui.input.commands;

import de.eldritch.discord.turtlecrawler.jda.JDAWrapper;
import de.eldritch.discord.turtlecrawler.util.logging.logback.JavaLoggingAppender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

public class CommandLevelJDA extends CommandLevel {
    @Override
    protected void get() {
        out("JDA log level is currently set to " + JavaLoggingAppender.jdaLevelFilter.getName());
    }

    @Override
    protected void set(Level level) {
        if (level != null) {
            JavaLoggingAppender.jdaLevelFilter = level;
            out("JDA log level has been set to " + level.getName());
        }
    }

    @Override
    public @NotNull List<String> usage() {
        return List.of(
                "level-jda",
                "level-jda list",
                "level-jda <level>"
        );
    }
}
