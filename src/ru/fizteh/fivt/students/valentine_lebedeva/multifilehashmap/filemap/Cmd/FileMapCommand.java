package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.Cmd;

import ru.fizteh.fivt.students.valentine_lebedeva.Table;

public abstract class FileMapCommand extends Command {
    public final boolean checkWorkTable(final Table workTable) {
        if (workTable == null) {
            System.out.println("no table");
            return false;
        } else {
            return true;
        }
    }
}
