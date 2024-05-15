package de.turtleboi.discord_crawler.job;

import de.turtleboi.discord_crawler.collection.Collector;
import de.turtleboi.discord_crawler.executor.Executor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import org.jetbrains.annotations.NotNull;

public record GuildJob(long guild) implements Job {
    @Override
    public void execute(@NotNull JDA jda, @NotNull Executor executor, @NotNull Collector collector) {
        Guild guild = jda.getGuildById(this.guild);

        if (guild == null)
            throw new NullPointerException("No such guild: " + this.guild);

        for (Channel channel : guild.getChannels())
            executor.queueJob(new ChannelJob(channel.getIdLong()));

        // TODO: metadata
    }
}
