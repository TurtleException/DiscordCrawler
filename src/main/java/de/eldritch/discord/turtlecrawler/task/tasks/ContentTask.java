package de.eldritch.discord.turtlecrawler.task.tasks;

import de.eldritch.discord.turtlecrawler.Main;
import de.eldritch.discord.turtlecrawler.task.Task;
import de.eldritch.discord.turtlecrawler.task.TaskManager;
import net.dv8tion.jda.api.exceptions.HttpException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.logging.Level;

/**
 * A Task that interacts with the Discord Content Delivery Network (CDN).
 */
public class ContentTask extends Task {
    private final String url;
    private final File file;

    public ContentTask(@NotNull TaskManager manager, @NotNull String url) {
        super(manager, "CONTENT/" + stripPrefix(url));
        this.url = url;

        this.file = new File(manager.getDIR(), buildPath(url));
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void run() {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to create output file", e);
            return;
        }

        OkHttpClient httpClient = Main.singleton.getJDAWrapper().getJDA().getHttpClient();

        Request request = new Request.Builder().url(url).build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful())
                throw new HttpException("Request failed with code " + response.code() + ": " + response.message());
            logger.log(Level.FINE, "Request succeeded with code " + response.code() + ": " + response.message());

            if (response.body() == null)
                throw new NullPointerException("Empty response body");

            FileOutputStream stream = new FileOutputStream(file);
            response.body().byteStream().transferTo(stream);
            stream.close();
        } catch (IOException | HttpException | NullPointerException e) {
            logger.log(Level.WARNING, "Failed to download content from " + url, e);
            return;
        }
        logger.log(Level.FINE, "File " + stripPrefix(url) + " downloaded.");
    }

    /**
     * Returns a String that is equal to the provided String but without the common leading Discord CDN url.
     * @param url URL that should be shortened.
     * @return URL without Discord CDN prefix.
     */
    private static String stripPrefix(@NotNull String url) {
        String prefix = "https://cdn.discordapp.com/";
        return url.startsWith(prefix) ? url.substring(prefix.length()) : url;
    }

    /**
     * Provides the path to where the File should be saved.
     * @param url URL to the CDN.
     * @return Built path.
     */
    private static String buildPath(@NotNull String url) {
        return "content" + File.separator + stripPrefix(url).replaceAll("/", File.separator);
    }
}
