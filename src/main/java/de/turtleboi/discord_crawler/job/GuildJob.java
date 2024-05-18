package de.turtleboi.discord_crawler.job;

import de.turtleboi.discord_crawler.collection.Collector;
import de.turtleboi.discord_crawler.executor.Executor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.requests.Route;
import org.jetbrains.annotations.NotNull;

public record GuildJob(
        long guild
) implements Job {
    @Override
    public void execute(@NotNull JDA jda, @NotNull Executor executor, @NotNull Collector collector) {
        Guild guild = jda.getGuildById(this.guild);

        if (guild == null)
            throw new NullPointerException("No such guild: " + this.guild);

        for (Channel channel : guild.getChannels())
            executor.queueJob(new ChannelJob("guilds/" + this.guild + "/channels/" + channel.getId(), channel.getIdLong()));

        String guildId = String.valueOf(this.guild);

        executor.queueJob(new DataObjectJob("guilds/" + guildId + "/guild"         , Route.Guilds.GET_GUILD.compile(guildId)));
        executor.queueJob(new DataObjectJob("guilds/" + guildId + "/audit_logs"    , Route.Guilds.GET_AUDIT_LOGS.compile(guildId)));
        executor.queueJob(new DataObjectJob("guilds/" + guildId + "/guild_embed"   , Route.Guilds.GET_GUILD_EMBED.compile(guildId)));
        executor.queueJob(new DataObjectJob("guilds/" + guildId + "/welcome_screen", Route.Guilds.GET_WELCOME_SCREEN.compile(guildId)));
        executor.queueJob(new DataArrayJob("guilds/" + guildId + "/channels"     , Route.Guilds.GET_CHANNELS.compile(guildId)));
        executor.queueJob(new DataArrayJob("guilds/" + guildId + "/bans"         , Route.Guilds.GET_BANS.compile(guildId)));
        executor.queueJob(new DataArrayJob("guilds/" + guildId + "/webhooks"     , Route.Guilds.GET_WEBHOOKS.compile(guildId)));
        executor.queueJob(new DataArrayJob("guilds/" + guildId + "/emojis"       , Route.Guilds.GET_GUILD_EMOJIS.compile(guildId)));
        executor.queueJob(new DataArrayJob("guilds/" + guildId + "/voice_regions", Route.Guilds.GET_VOICE_REGIONS.compile(guildId)));
        executor.queueJob(new DataArrayJob("guilds/" + guildId + "/integrations" , Route.Guilds.GET_INTEGRATIONS.compile(guildId)));
        executor.queueJob(new DataArrayJob("guilds/" + guildId + "/events"       , Route.Guilds.GET_SCHEDULED_EVENTS.compile(guildId)));
    }
}
