package de.turtleboi.discord_crawler;

import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public class Main {
    public static final String VERSION;

    static {
        System.out.print("Starting DiscordCrawler");

        try (InputStream r = Main.class.getResourceAsStream("meta.properties")) {
            Properties metaProperties = new Properties();
            metaProperties.load(r);

            VERSION = metaProperties.getProperty("version");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Called by the JVM when starting this program.
     */
    public static void main(String[] args) {
        /* VERSION*/
        if (VERSION == null)
            throw new Error("Version may not be null. This application was not built properly.");
        System.out.printf(" version %s...%n", VERSION);

        int exitCode = new CommandLine(new CrawlCommand()).execute(args);
        System.exit(exitCode);
    }
}
