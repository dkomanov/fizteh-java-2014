package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.shell;


import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBTable;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.MultiFileHashMap;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;

import java.util.ArrayList;

public class CommandExit extends Command {
    private MultiFileHashMap multiFileHashMap;

    public CommandExit(MultiFileHashMap newMultiFileHashMap) {
        multiFileHashMap = newMultiFileHashMap;
    }

    public void execute(ArrayList<String> args) throws Exception {
        if (multiFileHashMap.getWorkingDBTable() != null) {
            multiFileHashMap.getWorkingDBTable().writeToFile();
        }

        System.exit(0);
    }

}
