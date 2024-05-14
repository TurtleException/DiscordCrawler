package de.turtleboi.discord_crawler.job;

import de.turtleboi.discord_crawler.collection.Collector;
import de.turtleboi.discord_crawler.executor.Executor;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

public record ChannelJob(long channel) implements Job {
    @Override
    public void execute(@NotNull JDA jda, @NotNull Executor executor, @NotNull Collector collector) {
        // TODO
    }
}
