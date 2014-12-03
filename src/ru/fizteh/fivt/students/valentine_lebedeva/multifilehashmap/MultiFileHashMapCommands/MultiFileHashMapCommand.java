package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands;

import ru.fizteh.fivt.students.valentine_lebedeva.Table;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.Cmd.Command;

public abstract class MultiFileHashMapCommand extends Command {
    public final boolean checkWorkTable(final Table workTable) {
        if (workTable == null) {
            System.out.println("no table");
            return false;
        } else {
            return true;
        }
    }

}
