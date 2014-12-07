package ru.fizteh.fivt.students.andrey_reshetnikov.fileMap;

/**
 * Created by Hoderu on 12.10.14.
 */
public interface CommandContainer {
    Command getCommandByName(String s) throws UnknownCommand;
}
