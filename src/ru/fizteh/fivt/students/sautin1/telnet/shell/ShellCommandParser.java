package ru.fizteh.fivt.students.sautin1.telnet.shell;

/**
 * Class for parsing shell commands. Shell commands are stored in one string divided by COMMAND_DELIMITER.
 * Commands are divided into parameters by PARAM_DELIMITER.
 * Created by sautin1 on 10/4/14.
 */
public class ShellCommandParser implements CommandParser {
    private static final String COMMAND_DELIMITER = "\\s*;\\s*";
    private static final String PARAM_DELIMITER = "\\s+";

    /**
     * Converts array of strings into one string.
     * @param stringArray - array of strings.
     * @return string - concatenation of all the strings in stringArray
     */
    @Override
    public String convertArrayToString(String... stringArray) {
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
     * @return array of substrings of the string.
     */
    @Override
    public String[] splitStringIntoCommands(String string) {
        string = string.trim();
        return string.split(COMMAND_DELIMITER);
    }

    /**
     * Splits string of command with parameters into array of parameters.
     * @param string - string which must be split into parameters.
     * @return array of parameters.
     */
    @Override
    public String[] splitCommandIntoParams(String string) {
        return string.split(PARAM_DELIMITER);
    }
}
