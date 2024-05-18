package de.turtleboi.discord_crawler;

import de.turtleboi.discord_crawler.collection.Collector;
import de.turtleboi.discord_crawler.executor.Executor;
import de.turtleboi.discord_crawler.job.ChannelJob;
import de.turtleboi.discord_crawler.job.CompleteJob;
import de.turtleboi.discord_crawler.job.DataObjectJob;
import de.turtleboi.discord_crawler.job.GuildJob;
import de.turtleboi.discord_crawler.util.ConsoleUtil;
import net.dv8tion.jda.api.requests.Route;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@Command()
@SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray", "FieldMayBeFinal"})
public class CrawlCommand implements Callable<Integer> {
    @Option(names = {"-g", "--guild"}, description = "Snowflake ID of a guild to crawl", arity = "0..*")
    private long[] guilds = {};

    @Option(names = {"-c", "--channel"}, description = "Snowflake ID of a channel to crawl", arity = "0..*")
    private long[] channels = {};

    @Option(names = {"-v", "--verbose"}, description = "Increase verbosity", defaultValue = "false")
    private boolean verbose;

    @Option(names = {"-s", "--sparse"}, description = "Do not download media", defaultValue = "false")
    private boolean sparse;

    @Option(names = {"-f", "--file"}, description = "Output file")
    private File outputDir;

    @Option(names = {"-a", "--all"}, description = "Index all", defaultValue = "false")
    private boolean all;

    @Override
    public @NotNull Integer call() throws Exception {
        String token = ConsoleUtil.readPassword("Enter bot token");

        if (this.outputDir == null)
            this.outputDir = Main.getDefaultOutputDir();

        Executor  executor  = new Executor(token);
        Collector collector = new Collector(outputDir);

        executor.queueJob(new DataObjectJob("self", Route.Self.GET_SELF.compile()));

        if (all) {
            executor.queueJob(new CompleteJob());
        } else {
            for (Long guild : this.guilds)
                executor.queueJob(new GuildJob(guild));
            for (Long channel : this.channels)
                executor.queueJob(new ChannelJob(channel));
        }

        executor.run(collector);

        return 0;
    }
}
