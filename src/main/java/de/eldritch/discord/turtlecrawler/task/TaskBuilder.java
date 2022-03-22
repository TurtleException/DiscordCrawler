package de.eldritch.discord.turtlecrawler.task;

import de.eldritch.discord.turtlecrawler.Main;
import de.eldritch.discord.turtlecrawler.task.tasks.ChannelTask;
import de.eldritch.discord.turtlecrawler.task.tasks.GuildTask;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.IOException;

/**
 * Simple helper class for creating {@link Task Tasks} and registering them to the {@link TaskManager}.
 */
public class TaskBuilder {
    public static void guildTask(Guild guild) {
        TaskManager manager = Main.singleton.getManager().newTaskManager();
        manager.register(new GuildTask(manager, guild));
    }

    public static void channelTask(MessageChannel channel) throws IOException {
        TaskManager manager = Main.singleton.getManager().newTaskManager();
        manager.register(new ChannelTask(manager, channel));
    }
}
