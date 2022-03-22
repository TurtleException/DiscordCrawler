package de.eldritch.discord.turtlecrawler.ui.input.commands;

import de.eldritch.discord.turtlecrawler.Config;
import de.eldritch.discord.turtlecrawler.DiscordTurtleCrawler;
import de.eldritch.discord.turtlecrawler.ui.input.Command;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;

/**
 * Command to interact with the {@link Config} via CLI.
 */
public class CommandConfig implements Command {
    @Override
    public void onInvoke(String[] args, String raw) {
        if (args.length < 1) {
            logWarn("Missing argument: Expected 'list' or <key>");
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            list();
        } else {
            String key = args[0];
            if (args.length > 1) {
                // update value
                String value = raw.substring(raw.indexOf(" ") + 1 + key.length());
                value = value.substring(value.indexOf(args[1]));

                Config.set(key, value);
                DiscordTurtleCrawler.LOGGER.log(Level.INFO, "Set key '" + key + "' to value '" + value + "'.");
            } else {
                // query value
                String value = String.valueOf(Config.get(key));

                DiscordTurtleCrawler.LOGGER.log(Level.INFO, "Key '" + key + "' currently has value '" + value + "'.");
            }
        }
    }

    /**
     * List all currently set keys of the config and print them to the console.
     */
    private void list() {
        StringBuilder builder = new StringBuilder("Config keys: ");
        for (String configKey : getConfigKeys())
            builder.append("  ").append(configKey);
        DiscordTurtleCrawler.LOGGER.log(Level.INFO, builder.toString());
    }

    /**
     * Provides a {@link List} of all currently set key of the config.
     * @return List of config keys.
     */
    private List<String> getConfigKeys() {
        return Config.values().stream().map(objectObjectEntry -> (String) objectObjectEntry.getKey()).toList();
    }

    @Override
    public @NotNull List<String> usage() {
        return List.of(
                "config list",
                "config save",
                "config get <key>",
                "config set <key> <value>"
        );
    }
}
