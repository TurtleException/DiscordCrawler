package de.eldritch.discord.turtlecrawler.util;

import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

public class MiscUtil {
    public static String getDIR(MessageChannel channel) {
        return (channel instanceof BaseGuildMessageChannel guildChannel
                ? "G" + guildChannel.getGuild().getId() + File.separator + "C"
                : "P") + channel.getId();
    }

    public static String getDIR(Guild guild) {
        return "G" + guild.getId();
    }

    public static void await(@NotNull BooleanSupplier condition, int timeout, @NotNull TimeUnit unit) throws TimeoutException, InterruptedException {
        final long timeoutMillis = System.currentTimeMillis() + unit.toMillis(timeout);

        while (!condition.getAsBoolean()) {
            if (timeoutMillis >= System.currentTimeMillis())
                throw new TimeoutException("Timed out");
            Thread.sleep(20L);
        }
    }
}
