package de.eldritch.discord.turtlecrawler.task;

import de.eldritch.discord.turtlecrawler.Main;
import de.eldritch.discord.turtlecrawler.task.tasks.ChannelTask;
import de.eldritch.discord.turtlecrawler.task.tasks.GuildTask;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Simple helper class for creating {@link Task Tasks} and registering them to the {@link TaskManager}.
 */
public class TaskBuilder {
    /**
     * Creates a new {@link GuildTask} and registers it to a new {@link TaskManager}.
     * @param guild Guild that will be passed to the GuildTask
     */
    public static void guildTask(@NotNull Guild guild) {
        TaskManager manager = Main.singleton.getManager().newTaskManager();
        manager.register(new GuildTask(manager, guild));
    }

    /**
     * Creates a new {@link ChannelTask} and registers it to a new {@link TaskManager}.
     * @param channel Channel that will be passed to the ChannelTask
     */
    public static void channelTask(@NotNull MessageChannel channel) throws IOException {
        TaskManager manager = Main.singleton.getManager().newTaskManager();
        manager.register(new ChannelTask(manager, channel));
    }
}
