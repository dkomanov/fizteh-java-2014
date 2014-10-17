package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.shell;


import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBTable;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;

import java.util.ArrayList;

public class CommandExit extends Command {
    private DBTable dbTable;

    public void setDBTable(DBTable newDBTable) {
        dbTable = newDBTable;
    }

    public void execute(ArrayList<String> args) throws Exception {
        if (dbTable != null) {
            dbTable.writeToFile();
        }

        System.exit(0);
    }

}
