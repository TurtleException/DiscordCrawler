package de.eldritch.discord.turtlecrawler.ui.input.commands;

import de.eldritch.discord.turtlecrawler.task.tasks.ChannelTask;
import de.eldritch.discord.turtlecrawler.task.tasks.GuildTask;
import de.eldritch.discord.turtlecrawler.task.Task;
import de.eldritch.discord.turtlecrawler.task.TaskBuilder;
import de.eldritch.discord.turtlecrawler.ui.input.Command;
import de.eldritch.discord.turtlecrawler.ui.input.CommandHelper;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * Command to build and register {@link Task Tasks}.
 */
public class CommandCrawl implements Command {
    @Override
    public void onInvoke(String[] args, String raw) {
        if (args.length < 1) {
            logWarn("Missing argument: Expected 'guild', 'channel' or 'private'");
            return;
        }

        if (args[0].equalsIgnoreCase("guild"))
            this.crawlGuild(args);
        else if (args[0].equalsIgnoreCase("channel"))
            this.crawlChannel(args);
        else if (args[0].equalsIgnoreCase("private"))
            this.crawlPrivate(args);
        else
            logWarn("Unknown argument '" + args[0] + "'. Expected 'guild', 'channel' or 'private'");
    }

    @Override
    public @NotNull List<String> usage() {
        return List.of(
                "crawl guild   <guild_snowflake>",
                "crawl channel <guild_snowflake>   <channel_snowflake>",
                "crawl private <channel_snowflake>"
        );
    }

    /**
     * Creates a {@link GuildTask} via {@link TaskBuilder}.
     * @param args Command arguments
     */
    private void crawlGuild(String[] args) {
        try {
            String guildID = args[1];
            Guild  guild   = CommandHelper.getGuild(guildID);
            TaskBuilder.guildTask(guild);
        } catch (ArrayIndexOutOfBoundsException e) {
            logWarn("Missing argument: guild_snowflake");
        } catch (NullPointerException | NumberFormatException e) {
            logWarn("Command failed", e);
        }
    }

    /**
     * Creates a {@link ChannelTask} with a designated {@link BaseGuildMessageChannel} (guild) via {@link TaskBuilder}.
     * @param args Command arguments
     */
    private void crawlChannel(String[] args) {
        Guild guild;
        try {
            String guildID = args[1];
            guild = CommandHelper.getGuild(guildID);
        } catch (ArrayIndexOutOfBoundsException e) {
            logWarn("Missing argument: guild_snowflake");
            return;
        } catch (NullPointerException | NumberFormatException e) {
            logWarn("Command failed", e);
            return;
        }

        BaseGuildMessageChannel channel;
        try {
            String channelID = args[2];
            channel = CommandHelper.getChannel(guild, channelID);
            TaskBuilder.channelTask(channel);
        } catch (ArrayIndexOutOfBoundsException e) {
            logWarn("Missing argument: channel_snowflake");
        } catch (NullPointerException | NumberFormatException | IOException e) {
            logWarn("Command failed", e);
        }
    }

    /**
     * Creates a {@link ChannelTask} with a designated {@link PrivateChannel} (dm) via {@link TaskBuilder}.
     * @param args Command arguments
     */
    private void crawlPrivate(String[] args) {
        try {
            String         channelID = args[1];
            PrivateChannel channel   = CommandHelper.getChannel(channelID);
            TaskBuilder.channelTask(channel);
        } catch (ArrayIndexOutOfBoundsException e) {
            logWarn("Missing argument: channel_snowflake");
        } catch (NullPointerException | NumberFormatException | IOException e) {
            logWarn("Command failed", e);
        }
    }
}
