package de.eldritch.discord.turtlecrawler.ui.input.commands;

import de.eldritch.discord.turtlecrawler.DiscordTurtleCrawler;
import de.eldritch.discord.turtlecrawler.ui.input.Command;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

/**
 * Command to set the {@link Level} of {@link DiscordTurtleCrawler#LOGGER}.
 */
public class CommandLevel implements Command {
    @Override
    public void onInvoke(String[] args, String raw) {
        if (args.length < 1) {
            DiscordTurtleCrawler.LOGGER.log(Level.ALL, "Log level is currently set to " + DiscordTurtleCrawler.LOGGER.getLevel().getName());
            return;
        }

        Level level = null;
        String str = args[0].toUpperCase();

        switch (str) {
            case "LIST"            -> list();
            case "ALL"             -> level = Level.ALL;
            case "FINEST"          -> level = Level.FINEST;
            case "FINER"           -> level = Level.FINER;
            case "FINE"            -> level = Level.FINE;
            case "INFO", "DEFAULT" -> level = Level.INFO;
            case "WARNING"         -> level = Level.WARNING;
            case "SEVERE"          -> level = Level.SEVERE;
            case "OFF"             -> level = Level.OFF;
            default -> DiscordTurtleCrawler.LOGGER.log(Level.ALL, "Unknown argument: '" + args[0] + "'.");
        }

        if (level != null) {
            DiscordTurtleCrawler.LOGGER.setLevel(level);
            DiscordTurtleCrawler.LOGGER.log(Level.ALL, "Log level has been set to " + level.getName());
        }
    }

    /**
     * Lists all available log levels and prints them to the console.
     */
    private void list() {
        DiscordTurtleCrawler.LOGGER.log(Level.ALL, "Log levels:  <ALL|FINEST|FINER|FINE|INFO|DEFAULT|WARNING|SEVERE|OFF>");
    }

    @Override
    public @NotNull List<String> usage() {
        return List.of(
                "level list",
                "level <level>"
        );
    }
}
