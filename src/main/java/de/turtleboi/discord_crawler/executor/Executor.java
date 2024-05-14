package de.turtleboi.discord_crawler.executor;

import de.turtleboi.discord_crawler.collection.Collector;
import de.turtleboi.discord_crawler.job.CompleteJob;
import de.turtleboi.discord_crawler.job.Job;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Executor {
    private final Queue<Job> jobQueue;
    private final String token;

    public Executor(@NotNull String token) {
        this.jobQueue = new LinkedBlockingQueue<>();
        this.token = token;
    }

    public void queueJob(@NotNull Job job) {
        this.jobQueue.add(job);
    }

    public @NotNull Collector run() throws InterruptedException {
        if (this.jobQueue.isEmpty())
            this.jobQueue.add(new CompleteJob());

        Collector collector = new Collector();

        JDA jda = JDABuilder.create(token, GatewayIntent.getIntents(GatewayIntent.DEFAULT))
                .build();
        jda.awaitReady();

        while (!this.jobQueue.isEmpty()) {
            Job job = this.jobQueue.poll();
            job.execute(jda, this, collector);
        }

        return collector;
    }
}
