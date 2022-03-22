package de.eldritch.discord.turtlecrawler.ui.input;

import de.eldritch.discord.turtlecrawler.DiscordTurtleCrawler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

/**
 * An executable CLI command.
 */
public interface Command {
    /**
     * Called to invoke the command with the given arguments.
     * @param args Command arguments.
     */
    void onInvoke(String[] args, String raw);

    /**
     * Provides a {@link List} of {@link String Strings} explaining how to use the command.
     * @return Usage help.
     */
    @NotNull List<String> usage();

    /**
     * Logs a message with level {@link Level#WARNING} in the global logger. This is a shortcut for easier
     * implementation.
     * @param msg Log message.
     */
    default void logWarn(String msg) {
        DiscordTurtleCrawler.LOGGER.log(Level.WARNING, msg);
    }

    /**
     * Logs a message with level {@link Level#WARNING} in the global logger. This is a shortcut for easier
     * implementation.
     * @param msg Log message.
     * @param thrown Throwable to append to the log record.
     */
    default void logWarn(String msg, Throwable thrown) {
        DiscordTurtleCrawler.LOGGER.log(Level.WARNING, msg, thrown);
    }
}
