package de.turtleboi.discord_crawler.job;

import de.turtleboi.discord_crawler.collection.Collector;
import de.turtleboi.discord_crawler.executor.Executor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import org.jetbrains.annotations.NotNull;

public record CompleteJob() implements Job {
    @Override
    public void execute(@NotNull JDA jda, @NotNull Executor executor, @NotNull Collector collector) {
        for (Guild guild : jda.getGuilds())
            executor.queueJob(new GuildJob(guild.getIdLong()));

        for (Channel channel : jda.getPrivateChannels())
            executor.queueJob(new ChannelJob(channel.getIdLong()));
    }
}
