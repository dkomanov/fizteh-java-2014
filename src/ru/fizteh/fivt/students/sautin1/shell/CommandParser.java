package ru.fizteh.fivt.students.sautin1.shell;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandParser {
    private static final String commandDelimiter = "\\s*;\\s*";
    private static final String paramDelimiter = "\\s+";

    public static String convertArrayToString(String... stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringArray) {
            stringBuilder.append(string);
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    public static String[] splitStringIntoCommands(String string) {
        string = string.trim();
        return string.split(commandDelimiter);
    }

    public static String[] splitCommandIntoParams(String string) {
        return string.split(paramDelimiter);
    }
}
