package ru.fizteh.fivt.students.kotsurba.multifilehashmap;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public class ShellDbGet extends SimpleShellCommand {
    private DataBaseTable dataBase;

    public ShellDbGet(final DataBaseTable newBase) {
        setName("get");
        setNumberOfArgs(2);
        setHint("usage: get <key>");
        dataBase = newBase;
    }

    @Override
    public void run() {
        if (!dataBase.exist()) {
            System.out.println("no table");
            return;
        }
        String str = dataBase.get(getArg(1));
        if (str == null || str.isEmpty()) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(str);
        }
    }
}
