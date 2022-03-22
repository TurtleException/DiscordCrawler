package de.eldritch.discord.turtlecrawler;

/**
 * Technical main class used to initialize {@link DiscordTurtleCrawler} and to prevent accidentally initializing
 * multiple instances. In that case {@link Main#main(String[])} throws an {@link IllegalStateException}. While multiple
 * instances might not necessarily be a problem they are not permitted for simplification purposes.
 */
public class Main {
    public static DiscordTurtleCrawler singleton;

    /**
     * Called by the JVM when starting this program.
     */
    public static void main(String[] args) throws Exception {
        if (singleton == null)
            singleton = new DiscordTurtleCrawler();
        else
            throw new IllegalStateException("Cannot initialize multiple times.");

        singleton.run();
    }
}
