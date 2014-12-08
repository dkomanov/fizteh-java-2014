package ru.fizteh.fivt.students.kotsurba.multifilehashmap;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public class ShellCreateTable extends SimpleShellCommand {
    private DataBaseTable table;

    public ShellCreateTable(DataBaseTable newTable) {
        table = newTable;
        setName("create");
        setNumberOfArgs(2);
        setHint("usage: create <table name>");
    }

    public void run() {
        if (table.createTable(getArg(1))) {
            System.out.println("created");
        } else {
            System.out.println(getArg(1) + " exists");
        }
    }
}
