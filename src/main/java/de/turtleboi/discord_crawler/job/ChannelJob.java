package de.turtleboi.discord_crawler.job;

import de.turtleboi.discord_crawler.collection.Collector;
import de.turtleboi.discord_crawler.executor.Executor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
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
        Channel channel = jda.getChannelById(Channel.class, this.channel);

        if (channel == null)
            throw new NullPointerException("No such channel: " + this.channel);

        String channelId = String.valueOf(this.channel);

        executor.queueJob(new DataObjectJob(collectorPath + "/channel", Route.Channels.GET_CHANNEL.compile(channelId)));

        if (channel instanceof IThreadContainer threadContainer) {
            executor.queueJob(new DataObjectJob(collectorPath + "/threads", Route.Channels.LIST_PUBLIC_ARCHIVED_THREADS.compile(channelId)));
        }
    }

    public boolean isGuild() {
        return this.collectorPath.startsWith("guilds");
    }
}
