package de.eldritch.discord.turtlecrawler.util;

import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.File;

public class MiscUtil {
    public static String getDIR(MessageChannel channel) {
        return (channel instanceof BaseGuildMessageChannel guildChannel
                ? "G" + guildChannel.getGuild().getId() + File.separator + "C"
                : "P") + channel.getId();
    }

    public static String getDIR(Guild guild) {
        return "G" + guild.getId();
    }
}
