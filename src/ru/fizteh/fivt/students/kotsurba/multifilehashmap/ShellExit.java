package ru.fizteh.fivt.students.kotsurba.multifilehashmap;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public class ShellExit extends SimpleShellCommand {

    private DataBaseTable table;

    public ShellExit(DataBaseTable newTable) {
        table = newTable;
        setName("exit");
        setNumberOfArgs(1);
        setHint("usage: exit");
    }

    @Override
    public void run() {
        table.exit();
        System.exit(0);
    }

}
