package de.turtleboi.discord_crawler.util;

import org.jetbrains.annotations.NotNull;

import java.io.Console;
import java.util.Scanner;

public class ConsoleUtil {
    private ConsoleUtil() { }

    public static String readPassword(@NotNull String prompt) {
        prompt = prompt + " >  ";

        Console console = System.console();
        if (console != null)
            return new String(console.readPassword(prompt));

        // workaround for IDEs

        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return scanner.nextLine();
    }
}
