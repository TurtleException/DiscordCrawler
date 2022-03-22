package de.eldritch.discord.turtlecrawler;

import de.eldritch.discord.turtlecrawler.jda.JDAWrapper;
import de.eldritch.discord.turtlecrawler.task.TaskManager;
import de.eldritch.discord.turtlecrawler.ui.input.Receiver;
import de.eldritch.discord.turtlecrawler.util.Status;
import de.eldritch.discord.turtlecrawler.util.logging.SystemOutputToggleLogger;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * The actual main class of the program.
 * @see Main
 */
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
            LOGGER.log(Level.SEVERE, "Failed to declare directory", e);
        }
        DIR = f;
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

        jdaWrapper = new JDAWrapper();
        manager = new DistributionManager();
        receiver = new Receiver();

        /* ----- RUNNING ----- */

        Scanner scanner = new Scanner(System.in);

        status.set(Status.RUNNING);
        while (status.get() == Status.RUNNING) {
            if (scanner.hasNextLine()) {
                receiver.receive(scanner.nextLine());
            }
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

        // TODO: save & exit
    }

    /* ----- GETTERS ----- */

    public DistributionManager getManager() throws IllegalStateException {
        if (status.get() != Status.RUNNING)
            throw new IllegalStateException("DistributionManager is only available during status RUNNING.");

        return manager;
    }

    public JDAWrapper getJDAWrapper() {
        return jdaWrapper;
    }
}
