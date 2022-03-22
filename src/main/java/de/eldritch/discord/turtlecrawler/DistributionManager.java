package de.eldritch.discord.turtlecrawler;

import de.eldritch.discord.turtlecrawler.task.TaskManager;
import de.eldritch.discord.turtlecrawler.util.logging.NestedToggleLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class DistributionManager {
    private final NestedToggleLogger logger;
    private final DiscordTurtleCrawler main;

    private HashSet<TaskManager> taskManagers = new HashSet<>();

    public static final File OUTPUT_DIR = new File(DiscordTurtleCrawler.DIR, "out");
    static {
        //noinspection ResultOfMethodCallIgnored
        OUTPUT_DIR.mkdir();
    }

    DistributionManager(@NotNull DiscordTurtleCrawler main) {
        this.main = main;

        logger = new NestedToggleLogger("DIST MANAGER", DiscordTurtleCrawler.LOGGER);
    }

    public TaskManager newTaskManager() {
        Instant instant = Instant.now();

        String name = DateTimeFormatter.ISO_DATE.format(instant);

        // retrieve increment
        List<Integer> increments = getOutputDirs().stream()
                .filter(file -> file.getName().startsWith(name))
                .map(file -> file.getName().substring(name.length() + 1))
                .map(str  -> {
                    try {
                        return Integer.parseInt(str);
                    } catch (NumberFormatException e) {
                        return -1;
                    }
                })
                .filter(integer -> integer >= 0).toList();
        int increment = 0;
        while (increments.contains(increment))
            increment++;

        TaskManager manager =  new TaskManager(name + "-" + increment);
        taskManagers.add(manager);
        return manager;
    }

    private List<File> getOutputDirs() {
        File[] arr = OUTPUT_DIR.listFiles((dir, name) -> Pattern.matches("", name));
        return arr != null ? Arrays.stream(arr).filter(File::isDirectory).toList() : List.of();
    }

    public Set<TaskManager> getTaskManagers() {
        return Set.copyOf(taskManagers);
    }
}
