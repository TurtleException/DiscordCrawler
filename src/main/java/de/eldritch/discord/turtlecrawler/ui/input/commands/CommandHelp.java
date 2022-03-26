package de.eldritch.discord.turtlecrawler.ui.input.commands;

import de.eldritch.discord.turtlecrawler.ui.input.Command;
import de.eldritch.discord.turtlecrawler.ui.input.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandHelp implements Command {
    @Override
    public void onInvoke(String[] args, String raw) {
        if (args.length < 1) {
            out("Commands:  < " + String.join(" | ", Commands.getCommands().keySet()) + " >");
            return;
        }

        Map<String, Command> commands = Commands.getCommands();

        Optional<String> command = commands.keySet().stream().filter(s -> s.equalsIgnoreCase(args[0])).findFirst();

        if (command.isPresent()) {
            List<String> usage = commands.get(command.get()).usage();

            out("# # # # # Usage of '" + command.get() + "':");
            for (String s : usage) {
                out("# " + s);
            }
            out("# # # # #");
        } else {
            out("Unknown argument: '" + args[0] + "'.");
        }
    }

    @Override
    public @NotNull List<String> usage() {
        return List.of(
                "help",
                "help <command>"
        );
    }
}
