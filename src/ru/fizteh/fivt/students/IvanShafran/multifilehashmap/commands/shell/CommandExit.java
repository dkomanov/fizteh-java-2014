package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.shell;


import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBFile;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;

import java.util.ArrayList;

public class CommandExit extends Command {

    public void execute(ArrayList<String> args) {
        System.exit(0);
    }

}
