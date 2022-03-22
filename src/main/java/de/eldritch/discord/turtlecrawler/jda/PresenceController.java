package de.eldritch.discord.turtlecrawler.jda;

import de.eldritch.discord.turtlecrawler.Main;
import de.eldritch.discord.turtlecrawler.task.TaskManager;
import de.eldritch.discord.turtlecrawler.util.logging.NestedToggleLogger;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.logging.Level;

public class PresenceController extends Thread {
    private final JDAWrapper wrapper;
    private final NestedToggleLogger logger;

    /**
     * Indicates what information the {@link Activity} should display next. Switches with each iteration.
     */
    private boolean toggle;

    PresenceController(@NotNull JDAWrapper wrapper) {
        this.wrapper = wrapper;
        this.logger = new NestedToggleLogger("PRESENCE CONTROLLER", JDAWrapper.LOGGER);

        this.setName("PRESENCE CONTROLLER");
        this.setDaemon(true);
        this.start();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            this.updatePresence();

            // wait 10 seconds
            try {
                this.wait(1000L);
            } catch (InterruptedException e) {
                logger.log(Level.FINE, "Thread interrupted while waiting", e);
            }
        }
    }

    /**
     * Queries all necessary information and updates the bot presence.
     */
    private void updatePresence() {
        toggle = !toggle;

        Set<TaskManager> taskManagers = Main.singleton.getManager().getTaskManagers();

        /* ----- STATS ----- */
        boolean working  = !taskManagers.isEmpty();
        long    tasks    = 0;
        long    messages = 0;
        /* ----- ----- ----- */
        for (TaskManager taskManager : taskManagers) {
            tasks    += taskManager.getTaskAmount();
            messages += taskManager.getTotalProcessedMessages();
        }

        /* ----- MODIFY PRESENCE ----- */
        OnlineStatus status = (working ? OnlineStatus.ONLINE : OnlineStatus.IDLE);
        Activity activity = working
                ? (toggle
                    ? Activity.of(Activity.ActivityType.PLAYING, tasks + " tasks.")
                    : Activity.of(Activity.ActivityType.WATCHING, messages + " messages."))
                : null;

        wrapper.getJDA().getPresence().setPresence(status, activity);
    }
}
