package ru.fizteh.fivt.students.kotsurba.multifilehashmap;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public final class ShellDbRemove extends SimpleShellCommand {
    private DataBaseTable dataBase;

    public ShellDbRemove(final DataBaseTable newDataBase) {
        setName("remove");
        setNumberOfArgs(2);
        setHint("usage: remove <key>");
        dataBase = newDataBase;
    }

    @Override
    public void run() {
        if (!dataBase.exist()) {
            System.out.println("no table");
            return;
        }
        if (!dataBase.remove(getArg(1))) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
