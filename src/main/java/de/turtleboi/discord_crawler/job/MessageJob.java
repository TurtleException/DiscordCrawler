package de.turtleboi.discord_crawler.job;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.turtleboi.discord_crawler.collection.Collector;
import de.turtleboi.discord_crawler.executor.Executor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import org.jetbrains.annotations.NotNull;

import static de.turtleboi.discord_crawler.collection.Collector.GSON;

public record MessageJob(
        @NotNull String collectorPath,
        long channel,
        long after
) implements Job {
    @Override
    public void execute(@NotNull JDA jda, @NotNull Executor executor, @NotNull Collector collector) {
        String channelId = String.valueOf(channel);
        String   afterId = String.valueOf(after);

        Route.CompiledRoute route = Route.Messages.GET_MESSAGE_HISTORY.compile(channelId).withQueryParams("after", afterId, "limit", "100");
        RestActionImpl<DataArray> restAction = new RestActionImpl<>(jda, route, (response, request) -> response.getArray());

        // TODO: process attachments

        DataArray data = restAction.complete();
        JsonElement[] elements = new JsonElement[data.length()];

        for (int i = 0, j = data.length() - 1; i < data.length(); i++, j--) {
            final DataObject obj  = data.getObject(i);
            final String     str  = obj.toString();
            final JsonObject json = GSON.fromJson(str, JsonObject.class);

            elements[j] = json;
        }

        collector.writeToArray(collectorPath + "/messages.json", elements);

        if (data.length() < 100) {
            // flush buffered array (all messages processed)
            collector.flushArray(collectorPath + "/messages.json");
        } else {
            // queue next message job
            final long lastId = data.getObject(0).getLong("id");
            executor.queueJob(new MessageJob(collectorPath, channel, lastId));
        }
    }
}
