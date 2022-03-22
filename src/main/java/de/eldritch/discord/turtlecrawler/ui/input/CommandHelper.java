package de.eldritch.discord.turtlecrawler.ui.input;

import de.eldritch.discord.turtlecrawler.Main;
import de.eldritch.discord.turtlecrawler.task.Task;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Provides helper methods for {@link Task} building.
 */
public class CommandHelper {
    /**
     * Provides a {@link Guild} object from a snowflake ID.
     * @param snowflake ID of the guild.
     * @return
     * @throws NullPointerException
     * @throws NumberFormatException
     */
    public static Guild getGuild(String snowflake) throws NullPointerException, NumberFormatException {
        if (snowflake == null)
            throw new NullPointerException("Snowflake may not be null.");

        Guild guild = Main.singleton.getJDAWrapper().getJDA().getGuildById(snowflake);

        if (guild == null)
            throw new NullPointerException("Could not find guild matching snowflake '" + snowflake + "'.");

        return guild;
    }

    /**
     * Provides a {@link TextChannel} object from a {@link Guild} and a snowflake ID.
     * @param guild
     * @param snowflake ID of the channel.
     * @return
     * @throws NullPointerException
     * @throws NumberFormatException
     */
    public static BaseGuildMessageChannel getChannel(Guild guild, String snowflake) throws NullPointerException, NumberFormatException {
        if (snowflake == null)
            throw new NullPointerException("Snowflake may not be null.");

        BaseGuildMessageChannel channel = guild.getChannelById(BaseGuildMessageChannel.class, snowflake);

        if (channel == null)
            throw new NullPointerException("Could not find TextChannel matching snowflake '" + snowflake + "' on guild " + guild.getId() + ".");

        return channel;
    }

    /**
     * Provides a {@link PrivateChannel} object from a snowflake ID.
     * @param snowflake ID of the channel.
     * @return
     * @throws NullPointerException
     * @throws NumberFormatException
     */
    public static PrivateChannel getChannel(String snowflake) throws NullPointerException, NumberFormatException {
        if (snowflake == null)
            throw new NullPointerException("Snowflake may not be null.");

        PrivateChannel channel = Main.singleton.getJDAWrapper().getJDA().getPrivateChannelById(snowflake);

        if (channel == null)
            throw new NullPointerException("Could not find PrivateChannel matching snowflake '" + snowflake + "'.");

        return channel;
    }
}
