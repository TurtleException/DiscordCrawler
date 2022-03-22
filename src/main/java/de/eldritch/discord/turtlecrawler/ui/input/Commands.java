package de.eldritch.discord.turtlecrawler.ui.input;

import de.eldritch.discord.turtlecrawler.DiscordTurtleCrawler;
import de.eldritch.discord.turtlecrawler.ui.input.commands.CommandConfig;
import de.eldritch.discord.turtlecrawler.ui.input.commands.CommandCrawl;
import de.eldritch.discord.turtlecrawler.ui.input.commands.CommandLevel;

import java.util.HashMap;

/**
 * Both a collection of all available CLI commands and responsible for the basic logic behind command management.
 * @see Receiver
 * @see Commands#onCommand(String, String[], String)
 */
public class Commands {
    /**
     * Static map of commands with their corresponding {@link String} representation as key and the specific
     * {@link Command} implementation as value.
     */
    private static final HashMap<String, Command> commands = new HashMap<>();

    // class initializer to have commands available statically
    static {
        commands.put("config", new CommandConfig());
        commands.put("crawl" , new CommandCrawl());
        commands.put("level" , new CommandLevel());
    }

    /**
     * This method gets invoked by {@link Receiver#receive(String)} when no other method of input can be applied.
     * @param cmd String representation of the command.
     * @param args Arguments, excluding the content of cmd.
     * @param raw Raw input string provided by {@link DiscordTurtleCrawler} main loop.
     * @return Whether a command could be matched to the cmd string.
     */
    public static synchronized boolean onCommand(String cmd, String[] args, String raw) {
        Command command = commands.get(cmd);

        if (command != null) {
            command.onInvoke(args, raw);
            return true;
        } else {
            return false;
        }
    }
}
