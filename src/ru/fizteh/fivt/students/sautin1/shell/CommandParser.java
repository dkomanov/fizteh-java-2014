package ru.fizteh.fivt.students.sautin1.shell;

/**
 * Contains static methods to parse shell commands.
 * Created by sautin1 on 9/30/14.
 */
public class CommandParser {
    private static final String COMMAND_DELIMITER = "\\s*;\\s*";
    private static final String PARAM_DELIMITER = "\\s+";

    /**
     * Converts array of strings into one string.
     * @param stringArray - array of strings.
     * @return string - concatenation of all the strings in stringArray
     */
    public static String convertArrayToString(String... stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringArray) {
            stringBuilder.append(string);
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    /**
     * Splits string into commands with their parameters.
     * @param string - string which is split into commands+parameters.
     * @return array of strings respresenting the substrings of string.
     */
    public static String[] splitStringIntoCommands(String string) {
        string = string.trim();
        return string.split(COMMAND_DELIMITER);
    }

    public static String[] splitCommandIntoParams(String string) {
        return string.split(PARAM_DELIMITER);
    }
}
