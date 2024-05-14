package de.turtleboi.discord_crawler;

import org.jetbrains.annotations.NotNull;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "crawl")
public class CrawlCommand implements Callable<Integer> {
    @Option(names = {"-g", "--guild"}, description = "Snowflake ID of a guild to crawl")
    private Long guild;

    @Option(names = {"-c", "--channel"}, description = "Snowflake ID of a channel to crawl")
    private Long channel;

    @Option(names = {"-t", "--token"}, description = "Discord Bot token", required = true)
    private String token;

    @Option(names = {"-v", "--verbose"}, description = "Increase verbosity", defaultValue = "false")
    private boolean verbose;

    @Option(names = {"-s", "--sparse"}, description = "Do not download media", defaultValue = "false")
    private boolean sparse;

    @Override
    public @NotNull Integer call() throws Exception {
        // TODO

        return 0;
    }
}
