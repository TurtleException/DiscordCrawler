package de.turtleboi.discord_crawler.job;

import de.turtleboi.discord_crawler.collection.Collector;
import de.turtleboi.discord_crawler.executor.Executor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.Route;
import org.jetbrains.annotations.NotNull;

public record ChannelJob(
        @NotNull String collectorPath,
        long channel
) implements Job {
    public ChannelJob(long channel) {
        this("channels/" + channel, channel);
    }

    @Override
    public void execute(@NotNull JDA jda, @NotNull Executor executor, @NotNull Collector collector) {
        String channelId = String.valueOf(this.channel);

        executor.queueJob(new DataObjectJob(collectorPath + "/channel", Route.Channels.GET_CHANNEL.compile(channelId)));

        if (isGuild())
            executor.queueJob(new DataArrayJob(collectorPath + "/permissions", Route.Channels.GET_PERMISSIONS.compile(channelId)));
    }

    public boolean isGuild() {
        return this.collectorPath.startsWith("guilds");
    }
}
