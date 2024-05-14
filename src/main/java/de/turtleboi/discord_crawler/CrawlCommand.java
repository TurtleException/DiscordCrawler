package de.turtleboi.discord_crawler;

import de.turtleboi.discord_crawler.collection.Collector;
import de.turtleboi.discord_crawler.executor.Executor;
import de.turtleboi.discord_crawler.job.ChannelJob;
import de.turtleboi.discord_crawler.job.GuildJob;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "crawl")
@SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
public class CrawlCommand implements Callable<Integer> {
    @Option(names = {"-g", "--guild"}, description = "Snowflake ID of a guild to crawl", arity = "0..*")
    private long[] guilds;

    @Option(names = {"-c", "--channel"}, description = "Snowflake ID of a channel to crawl", arity = "0..*")
    private long[] channels;

    @Option(names = {"-t", "--token"}, description = "Discord Bot token", required = true)
    private String token;

    @Option(names = {"-v", "--verbose"}, description = "Increase verbosity", defaultValue = "false")
    private boolean verbose;

    @Option(names = {"-s", "--sparse"}, description = "Do not download media", defaultValue = "false")
    private boolean sparse;

    @Override
    public @NotNull Integer call() throws Exception {
        Executor executor = new Executor(token);

        for (Long guild : this.guilds)
            executor.queueJob(new GuildJob(guild));
        for (Long channel : this.channels)
            executor.queueJob(new ChannelJob(channel));

        Collector collector = executor.run();

        return 0;
    }
}
