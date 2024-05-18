package de.turtleboi.discord_crawler.job;

import de.turtleboi.discord_crawler.collection.Collector;
import de.turtleboi.discord_crawler.executor.Executor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import org.jetbrains.annotations.NotNull;

public record DataArrayJob(
        @NotNull String collectorPath,
        @NotNull Route.CompiledRoute route
) implements Job {
    @Override
    public void execute(@NotNull JDA jda, @NotNull Executor executor, @NotNull Collector collector) {
        RestActionImpl<String> restAction = new RestActionImpl<>(jda, route, (response, request) -> response.getArray().toPrettyString());

        String data = restAction.complete();

        collector.write(collectorPath + ".json", data, false);
    }
}
