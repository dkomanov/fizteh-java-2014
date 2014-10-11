package ru.fizteh.fivt.students.kotsurba.filemap.commands;

import ru.fizteh.fivt.students.kotsurba.filemap.database.DataBase;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public class ShellDbRemove extends SimpleShellCommand {
    private DataBase dataBase;

    public ShellDbRemove(final DataBase newDataBase) {
        setName("remove");
        setNumberOfArgs(2);
        setHint("usage: remove <key>");
        dataBase = newDataBase;
    }

    @Override
    public void run() {
        if (!dataBase.remove(getArg(1))) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
