package de.eldritch.discord.turtlecrawler;

import de.eldritch.discord.turtlecrawler.jda.JDAWrapper;
import de.eldritch.discord.turtlecrawler.task.TaskManager;
import de.eldritch.discord.turtlecrawler.ui.input.Receiver;
import de.eldritch.discord.turtlecrawler.util.Status;
import de.eldritch.discord.turtlecrawler.util.logging.LogUtil;
import de.eldritch.discord.turtlecrawler.util.logging.SimpleFormatter;
import de.eldritch.discord.turtlecrawler.util.logging.SystemOutputToggleLogger;
import net.dv8tion.jda.api.JDA;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * The actual main class of the program.
 * @see Main
 */
@SuppressWarnings("FieldCanBeLocal")
public class DiscordTurtleCrawler {
    private final Status status = new Status();

    public static final SystemOutputToggleLogger LOGGER = new SystemOutputToggleLogger("MAIN");

    /**
     * Directory in which the JAR is located.
     */
    public static final File DIR;
    static {
        File f = null;
        try {
            f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (URISyntaxException e) {
            System.out.println("Failed to declare directory.");
            e.printStackTrace();
        }
        DIR = f;
    }

    // Add FileHandler to LOGGER
    static {
        try {
            LOGGER.addHandler(LogUtil.getFileHandler(new SimpleFormatter()));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not register FileHandler", e);
        }
    }

    private JDAWrapper jdaWrapper;
    private DistributionManager manager;
    private Receiver receiver;

    /**
     * Main execution method, contains the main loop.
     * @see Main#main(String[])
     */
    void run() throws Exception {
        status.set(Status.INIT);

        LOGGER.log(Level.INFO, "Initializing JDRWrapper...");
        jdaWrapper = new JDAWrapper();

        LOGGER.log(Level.INFO, "Initializing DistributionManager...");
        manager = new DistributionManager(this);

        LOGGER.log(Level.INFO, "Initializing Receiver...");
        receiver = new Receiver();

        /* ----- RUNNING ----- */

        LOGGER.log(Level.INFO, "Startup done!");

        Scanner scanner = new Scanner(System.in);

        status.set(Status.RUNNING);
        while (status.get() == Status.RUNNING) {
            String line = scanner.nextLine();
            receiver.receive(line);
        }

        LOGGER.log(Level.WARNING, "Main loop has been interrupted.");

        this.shutdown();
    }

    /**
     * Notifies the program to exit the main loop and shut down.
     */
    public void exit() {
        status.set(Status.STOPPING);
    }

    /**
     * Indicates whether the main loop is currently active.
     * @return <code>true</code> if the main loop is running.
     */
    public boolean isRunning() {
        return status.get() == Status.RUNNING;
    }

    /**
     * Await execution of final tasks, proper shutdown of all active tasks and suspend all active threads.
     */
    private void shutdown() {
        if (status.get() <= Status.RUNNING)
            throw new IllegalStateException("Cannot shutdown while main loop is not yet or still running. Call exit() first!");

        LOGGER.log(Level.INFO, "Shutting down...");

        LOGGER.log(Level.INFO, "Notifying TaskManagers.");
        manager.getTaskManagers().forEach(TaskManager::shutdown);

        LOGGER.log(Level.INFO, "Notifying JDAWrapper.");
        jdaWrapper.shutdown();

        LOGGER.log(Level.INFO, "Notifying LOGGER.");
        LOGGER.log(Level.ALL, "OK bye.");
        LOGGER.shutdown();

        System.exit(0);
    }

    /* ----- GETTERS ----- */

    public DistributionManager getManager() throws IllegalStateException {
        if (status.get() != Status.RUNNING && status.get() != Status.STOPPING)
            throw new IllegalStateException("DistributionManager is only available during status RUNNING or STOPPING.");

        return manager;
    }

    public JDAWrapper getJDAWrapper() {
        return jdaWrapper;
    }
}
