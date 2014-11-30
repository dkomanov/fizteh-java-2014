package ru.fizteh.fivt.students.akhtyamovpavel.filemap.commands;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class CommandStorage {
    private String commandName;
    private ArrayList<String> argumentsList;

    public CommandStorage(final ArrayList<String> wordList) {
        commandName = wordList.get(0);
        wordList.remove(0);

        argumentsList = new ArrayList<String>(wordList);
    }

    public final String getCommandName() {
        return commandName;
    }

    public final ArrayList<String> getArgumentsList() {
        return argumentsList;
    }
}
