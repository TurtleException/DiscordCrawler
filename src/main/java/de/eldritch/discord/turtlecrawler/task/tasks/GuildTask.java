package de.eldritch.discord.turtlecrawler.task.tasks;

import de.eldritch.discord.turtlecrawler.task.Task;
import de.eldritch.discord.turtlecrawler.task.TaskManager;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

public class GuildTask extends Task {
    private final Guild guild;

    public GuildTask(@NotNull TaskManager manager, @NotNull Guild guild) {
        super(manager, "GUILD/" + guild.getId());
        this.guild = guild;
    }

    @Override
    public void run() {

    }
}
