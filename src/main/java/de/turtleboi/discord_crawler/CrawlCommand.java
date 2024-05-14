package de.turtleboi.discord_crawler;

import de.turtleboi.discord_crawler.job.ChannelJob;
import de.turtleboi.discord_crawler.job.CompleteJob;
import de.turtleboi.discord_crawler.job.GuildJob;
import de.turtleboi.discord_crawler.job.Job;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "crawl")
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
        List<Job> jobs = new ArrayList<>();

        for (Long guild : this.guilds)
            jobs.add(new GuildJob(guild));
        for (Long channel : this.channels)
            jobs.add(new ChannelJob(channel));
        if (jobs.isEmpty())
            jobs.add(new CompleteJob());

        // TODO: execute jobs

        return 0;
    }
}
