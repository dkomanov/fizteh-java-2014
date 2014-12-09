package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell;

import java.util.ArrayList;
import java.util.LinkedList;


public class CommandText {
    public String commandName;
    public ArrayList<String> arguments;

    public CommandText(LinkedList<String> commandWordsList) {
        commandName = commandWordsList.get(0);

        commandWordsList.remove(0);
        arguments = new ArrayList<String>(commandWordsList);
    }
}
