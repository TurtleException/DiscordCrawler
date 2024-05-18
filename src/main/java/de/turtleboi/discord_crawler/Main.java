package de.turtleboi.discord_crawler;

import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Main {
    public static final String VERSION;

    static {
        System.out.print("Starting DiscordCrawler");

        try (InputStream r = Main.class.getClassLoader().getResourceAsStream("meta.properties")) {
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

    @SuppressWarnings({"ResultOfMethodCallIgnored", "DataFlowIssue"})
    public static @NotNull File getDefaultOutputDir() throws IOException {
        try {
            File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File parentDir = jarFile.getParentFile();

            File outDir = new File(parentDir, "out");
            outDir.mkdirs();

            String timePrefix = DateTimeFormatter.ofPattern("uuuu-MM-dd").format(Instant.now().atZone(ZoneId.of("UTC")));

            List<Integer> increments = Arrays.stream(outDir.listFiles())
                    .filter(file -> file.getName().startsWith(timePrefix))
                    .map(file -> file.getName().substring(timePrefix.length() + 1))
                    .map(Integer::parseInt)
                    .toList();

            int increment = 0;
            while (increments.contains(increment))
                increment++;

            return new File(outDir, timePrefix + "-" + increment);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
}
