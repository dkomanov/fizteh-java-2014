package ru.fizteh.fivt.students.sautin1.shell;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandParser {
    private static final String COMMAND_DELIMITER = "\\s*;\\s*";
    private static final String PARAM_DELIMITER = "\\s+";

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
        return string.split(COMMAND_DELIMITER);
    }

    public static String[] splitCommandIntoParams(String string) {
        return string.split(PARAM_DELIMITER);
    }
}
