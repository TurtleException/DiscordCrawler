package de.eldritch.discord.turtlecrawler.jda;

import de.eldritch.discord.turtlecrawler.Config;
import de.eldritch.discord.turtlecrawler.DiscordTurtleCrawler;
import de.eldritch.discord.turtlecrawler.Main;
import de.eldritch.discord.turtlecrawler.util.logging.NestedToggleLogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

public class JDAWrapper {
    static final NestedToggleLogger LOGGER = new NestedToggleLogger("JDA", DiscordTurtleCrawler.LOGGER);
    /**
     * Singleton object to ensure instance uniqueness.
     */
    private static JDAWrapper singleton;

    /**
     * Constantly updates the {@link Presence} of the bot.
     */
    private PresenceController presenceController;

    /**
     * Discord API authorization token
     */
    private final String token;
    /**
     * JDA instance.
     * @see JDAWrapper#init()
     */
    private JDA jda;

    public JDAWrapper() throws LoginException, IOException {
        this.token = getDiscordToken();
        this.checkSingleton();
        this.init();

        presenceController = new PresenceController(this);

        // only declare singleton when new instance is successfully initialized
        singleton = this;
    }

    private void checkSingleton() {
        if (singleton != null) {
            LOGGER.log(Level.WARNING, "New instance declared!");
            LOGGER.log(Level.INFO, "Shutting down old instance...");

            this.shutdown();

            LOGGER.log(Level.INFO, "OK! New instance can now be safely initialized.");
        }
    }

    /**
     * Builds the {@link JDA} instance.
     * @throws LoginException
     */
    private void init() throws LoginException {
        LOGGER.log(Level.INFO, "Initializing...");
        JDABuilder builder = JDABuilder.createDefault(token);

        builder.setMemberCachePolicy(MemberCachePolicy.NONE);
        builder.setStatus(OnlineStatus.IDLE);
        builder.setActivity(null);

        LOGGER.log(Level.INFO, "Building JDA...");
        jda = builder.build();
        LOGGER.log(Level.INFO, "OK!");
    }

    /**
     * Attempts to shut down the {@link JDA} instance.
     */
    public void shutdown() {
        LOGGER.log(Level.WARNING, "Received shutdown command!");

        jda.shutdown();

        try {
            jda.awaitStatus(JDA.Status.SHUTDOWN);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Interrupted while shutting down.");
            jda.shutdownNow();
        } finally {
            LOGGER.log(Level.INFO, "Shutdown complete.");
        }

        jda = null;
    }

    /**
     * Provides the wrapped {@link JDA} instance. While this method is technically nullable the JDA is built when
     * constructing this wrapper. If building the JDA fails an exception is thrown.
     * @return JDA instance.
     */
    public JDA getJDA() {
        return jda;
    }

    private static String getDiscordToken() throws IOException {
        Properties properties = new Properties();

        File file = new File(DiscordTurtleCrawler.DIR, "token.properties");
        if (file.createNewFile()) {
            properties.setProperty("apiToken", "PLEASE PUT YOUR TOKEN HERE");
            properties.store(new FileWriter(file), null);
            return null;
        }

        properties.load(new FileReader(file));
        return properties.getProperty("apiToken", null);
    }
}
