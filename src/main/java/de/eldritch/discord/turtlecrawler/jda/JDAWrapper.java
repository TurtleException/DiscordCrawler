package de.eldritch.discord.turtlecrawler.jda;

import de.eldritch.discord.turtlecrawler.Config;
import de.eldritch.discord.turtlecrawler.DiscordTurtleCrawler;
import de.eldritch.discord.turtlecrawler.util.logging.NestedToggleLogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;

public class JDAWrapper {
    private static final NestedToggleLogger LOGGER = new NestedToggleLogger("JDA", DiscordTurtleCrawler.LOGGER);
    /**
     * Singleton object to ensure instance uniqueness.
     */
    private static JDAWrapper singleton;

    private final String token;
    private JDA jda;

    public JDAWrapper() throws LoginException {
        this.token = Config.DISCORD_TOKEN.get();
        this.checkSingleton();
        this.init();

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

    public JDA getJDA() {
        return jda;
    }
}
