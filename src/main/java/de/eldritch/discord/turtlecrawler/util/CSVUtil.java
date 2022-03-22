package de.eldritch.discord.turtlecrawler.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CSVUtil {
    public static String writeLine(String... values) {
        return Arrays.stream(values)
                .map(CSVUtil::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    private static String escapeSpecialCharacters(String str) {
        String str1 = str
                // replace quotes
                .replaceAll("\"", "\"\"")
                // replace newlines
                .replaceAll("\\R", "\\n");

        // wrap with quotes if string contains commas
        return (str1.contains(",")
                ? "\"" + str1 + "\""
                : str1);
    }
}
