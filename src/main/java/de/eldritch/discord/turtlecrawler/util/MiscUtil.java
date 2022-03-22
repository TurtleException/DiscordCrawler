package de.eldritch.discord.turtlecrawler.util;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;

public class MiscUtil {
    public static String getDIR(MessageChannel channel) {
        return (channel instanceof TextChannel guildChannel
                ? "G" + guildChannel.getGuild().getId() + File.separator + "C"
                : "P") + channel.getId();
    }
}
