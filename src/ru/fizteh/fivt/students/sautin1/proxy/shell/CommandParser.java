package ru.fizteh.fivt.students.sautin1.proxy.shell;

/**
 * Contains static methods to parse commands.
 * Created by sautin1 on 9/30/14.
 */
public interface CommandParser {
    String[] splitStringIntoCommands(String string);
    String[] splitCommandIntoParams(String string);
    String convertArrayToString(String... stringArray);
}
