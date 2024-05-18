package de.turtleboi.discord_crawler.collection;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Collector {
    private final File outputDir;

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
}
