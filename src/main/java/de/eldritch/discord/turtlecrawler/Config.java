package de.eldritch.discord.turtlecrawler;

import de.eldritch.discord.turtlecrawler.DiscordTurtleCrawler;
import de.eldritch.discord.turtlecrawler.ui.input.commands.CommandConfig;
import de.eldritch.discord.turtlecrawler.util.logging.NestedToggleLogger;
import de.eldritch.discord.turtlecrawler.util.logging.SystemOutputToggleLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Static configuration backed by the inner {@link KeyPair} class object.
 * <p>This config can be interacted with via the <code>config.properties</code> file in the working directory of the
 * JAR file or via the <code>config</code> command.
 * @see CommandConfig
 */
public class Config {
    public static class KeyPair<T> {
        private final @NotNull String key;
        private final T defaultValue;
        private T value;

        private KeyPair(@NotNull String key, T defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        /**
         * Provides the value that is currently assigned to this key.
         * @return Current value.
         */
        public T get() {
            return value != null ? value : defaultValue;
        }

        /**
         * Functions like {@link KeyPair#set(Object)} but of the value is <code>null</code> the default value will be
         * assigned as the new value.
         * @param obj New value.
         * @see Config#load()
         */
        private void load(@Nullable T obj) {
            value = ((obj != null) ? obj : defaultValue);
        }

        /**
         * Assigns a new value to the key.
         * @param obj New value.
         */
        public void set(T obj) {
            value = obj;
        }
    }

    /**
     * Authorization token for the Discord API.
     */
    public static final KeyPair<String>  DISCORD_TOKEN         = new KeyPair<>("discord.token", "null");
    /**
     * Capacity of the backlog of {@link NestedToggleLogger} and {@link SystemOutputToggleLogger}.
     */
    public static final KeyPair<Integer> LOGGER_QUEUE_CAPACITY = new KeyPair<>("logger.queueCapacity", 100);


    /**
     * File of the external configuration.
     */
    public static final File FILE = new File(DiscordTurtleCrawler.DIR, "config.properties");
    static {
        try {
            if (FILE.createNewFile())
                DiscordTurtleCrawler.LOGGER.log(Level.INFO, "Created new config file");
        } catch (IOException e) {
            DiscordTurtleCrawler.LOGGER.log(Level.WARNING, "Could not create new config file", e);
        }
    }


    /**
     * Saves all {@link KeyPair} objects to the {@link Properties} and stores them in the config file.
     */
    public static void save() {
        Properties properties = new Properties();

        properties.put(DISCORD_TOKEN.key, DISCORD_TOKEN.get());
        properties.put(LOGGER_QUEUE_CAPACITY.key, LOGGER_QUEUE_CAPACITY.get());

        try {
            properties.store(new FileWriter(FILE), null);
        } catch (IOException e) {
            DiscordTurtleCrawler.LOGGER.log(Level.WARNING, "Could not save config", e);
        }
    }

    /**
     * Attempts to load
     */
    public static void load() {
        Properties properties = new Properties();

        try {
            properties.load(new FileReader(FILE));
        } catch (IOException e) {
            DiscordTurtleCrawler.LOGGER.log(Level.WARNING, "Could not load config", e);
            return;
        }

        DISCORD_TOKEN.load(properties.getProperty(DISCORD_TOKEN.key));
        LOGGER_QUEUE_CAPACITY.load(
                properties.get(LOGGER_QUEUE_CAPACITY.key) instanceof Integer val ? val
                        : Integer.parseInt(properties.getProperty(LOGGER_QUEUE_CAPACITY.key))
        );
    }
}
