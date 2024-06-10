package de.turtleboi.discord_crawler.collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Collector {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private final File outputDir;

    private final ConcurrentHashMap<String, JsonArray> arrayBuffer = new ConcurrentHashMap<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Collector(@NotNull File outputDir) {
        this.outputDir = outputDir;
        this.outputDir.mkdirs();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void write(@NotNull String path, @NotNull String data, boolean append) {
        File file = new File(outputDir, path);
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file, append)) {
            writer.write(data);
        } catch (IOException e) {
            // TODO: handle
        }
    }

    public synchronized void writeToArray(@NotNull String path, @NotNull JsonElement... data) {
        JsonArray arr = this.arrayBuffer.computeIfAbsent(path, s -> new JsonArray());

        for (JsonElement element : data)
            arr.add(element);

        this.arrayBuffer.put(path, arr);
    }

    public void flushArray(@NotNull String path) {
        JsonArray arr = arrayBuffer.remove(path);

        if (arr == null) return;

        this.write(path, GSON.toJson(arr), false);
    }
}
