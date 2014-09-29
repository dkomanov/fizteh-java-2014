package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class CommandStorage {
    private String commandName;
    private ArrayList<String> argumentsList;

    public CommandStorage(ArrayList<String> wordList) {
        commandName = wordList.get(0);
        wordList.remove(0);

        argumentsList = new ArrayList<String>(wordList);
    }

    public String getCommandName() {
        return commandName;
    }

    public ArrayList<String> getArgumentsList() {
        return argumentsList;
    }
}
