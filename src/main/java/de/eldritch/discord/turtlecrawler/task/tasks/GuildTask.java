package de.eldritch.discord.turtlecrawler.task.tasks;

import de.eldritch.discord.turtlecrawler.task.Task;
import de.eldritch.discord.turtlecrawler.task.TaskManager;
import de.eldritch.discord.turtlecrawler.util.MiscUtil;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class GuildTask extends Task {
    private final Guild guild;

    private final File dir;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public GuildTask(@NotNull TaskManager manager, @NotNull Guild guild) {
        super(manager, "GUILD/G" + guild.getId());
        this.guild = guild;

        this.dir = new File(manager.getDIR(), MiscUtil.getDIR(guild));
        this.dir.mkdirs();
    }

    @Override
    public void run() {
        logger.log(Level.FINE, "Processing metadata...");

        this.handleMetadata("guild"        , Route.Guilds.GET_GUILD.compile(guild.getId()));
        this.handleMetadata("channels"     , Route.Guilds.GET_CHANNELS.compile(guild.getId()));
        this.handleMetadata("bans"         , Route.Guilds.GET_BANS.compile(guild.getId()));
        this.handleMetadata("webhooks"     , Route.Guilds.GET_WEBHOOKS.compile(guild.getId()));
        this.handleMetadata("embed"        , Route.Guilds.GET_GUILD_EMBED.compile(guild.getId()));
        this.handleMetadata("emotes"       , Route.Guilds.GET_GUILD_EMOTES.compile(guild.getId()));
        this.handleMetadata("audit_logs"   , Route.Guilds.GET_AUDIT_LOGS.compile(guild.getId()));
        this.handleMetadata("voice_regions", Route.Guilds.GET_VOICE_REGIONS.compile(guild.getId()));
        this.handleMetadata("integrations" , Route.Guilds.GET_INTEGRATIONS.compile(guild.getId()));
        this.handleMetadata("roles"        , Route.Roles.GET_ROLES.compile(guild.getId()));

        logger.log(Level.FINER, "Processing guild icon...");
        if (guild.getIconUrl() != null)
            manager.register(new ContentTask(manager, guild.getIconUrl()));

        logger.log(Level.FINER, "Processing guild banner...");
        if (guild.getBannerUrl() != null)
            manager.register(new ContentTask(manager, guild.getBannerUrl()));

        logger.log(Level.FINER, "Processing emotes...");
        for (Emote emote : guild.getEmotes())
            manager.register(new ContentTask(manager, emote.getImageUrl()));

        logger.log(Level.FINER, "Processing channels...");
        List<BaseGuildMessageChannel> channels = guild.getChannels().stream()
                .filter(BaseGuildMessageChannel.class::isInstance)
                .map(BaseGuildMessageChannel.class::cast)
                .toList();
        logger.log(Level.FINEST, channels.size() + " channels collected.");
        for (BaseGuildMessageChannel channel : channels) {
            logger.log(Level.FINEST, "Processing chanel " + channel.getId());
            try {
                manager.register(new ChannelTask(manager, channel));
                logger.log(Level.FINEST, "ChannelTask registered");
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not register ChannelTask for channel " + channel.getId(), e);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File provideFile(File dir, String filename) throws IOException {
        File file = new File(dir, filename);
        file.createNewFile();
        return file;
    }

    private void handleMetadata(String filename, Route.CompiledRoute route) {
        logger.log(Level.FINEST, "Requesting " + filename + " metadata...");

        File file;
        try {
            file = provideFile(dir, filename + ".json");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not open file for metadata '" + filename + "'.", e);
            return;
        }

        logger.log(Level.FINEST, "Querying API for " + filename + " metadata...");
        DataObject data = new RestActionImpl<DataObject>(guild.getJDA(), route, (response, dataObjectRequest) -> response.getObject()).complete();

        logger.log(Level.FINEST, "Saving data to file.");
        try {
            FileWriter writer = new FileWriter(file, false);
            writer.append(data.toPrettyString());
            writer.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Encountered an unexpected exception while writing file: " + file.getPath(), e);
            return;
        }

        logger.log(Level.FINEST, "Done processing " + filename + " metadata.");
    }
}
