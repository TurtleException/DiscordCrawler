package de.eldritch.discord.turtlecrawler.util.logging;

import de.eldritch.discord.turtlecrawler.DiscordTurtleCrawler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;

public class LogUtil {
    /**
     * After the main class requests the {@link FileHandler} via {@link LogUtil#getFileHandler(Formatter)} the log file
     * that is associated with that handler will be stored in this variable, so it can be used later.
     */
    private static File logFile;

    /**
     * Provides a {@link FileHandler} that is already set up for the running {@link DiscordTurtleCrawler} instance.
     * @param formatter Log {@link Formatter} to assign to the handler.
     * @return A {@link FileHandler} with the new log file.
     * @throws IOException if the handler fails to initialize.
     */
    public static @NotNull FileHandler getFileHandler(@NotNull Formatter formatter) throws IOException {
        String datePrefix = new SimpleDateFormat("yyyy-MM-dd'T'").format(Date.from(Instant.now()));

        // retrieve all existing log files from today
        File[] files = getLogDir().listFiles((dir, name) -> name.matches("^\\d\\d\\d\\d-\\d\\d-\\d\\dT_\\d+\\.log$") && name.startsWith(datePrefix));
        if (files == null)
            throw new NotDirectoryException(getLogDir().getName());

        String fileName = datePrefix + "_" + files.length + ".log";

        // create File
        logFile = new File(getLogDir(), fileName);


        // create FileHandler
        FileHandler fileHandler = new FileHandler(logFile.getPath(), true);
        fileHandler.setFormatter(formatter);
        return fileHandler;
    }

    /**
     * Provides the {@link File} that has been associated with the {@link FileHandler} by calling
     * {@link LogUtil#getFileHandler(Formatter)}. This may be null if the FileHandler has not yet been created.
     * @return Log file associated with the FileHandler of the global logger.
     */
    public static @Nullable File getLogFile() {
        return logFile;
    }

    /**
     * Provides the directory containing all log files.
     * @return log file directory.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getLogDir() {
        File file = new File(DiscordTurtleCrawler.DIR, "logs");
        file.mkdir();
        return file;
    }
}