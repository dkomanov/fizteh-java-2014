package ru.fizteh.fivt.students.kotsurba.multifilehashmap;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

import java.io.IOException;

public class ShellUseTable extends SimpleShellCommand {
    private DataBaseTable table;

    public ShellUseTable(DataBaseTable newTable) {
        table = newTable;
        setName("use");
        setNumberOfArgs(2);
        setHint("usage: use <table name>");
    }

    public void run() {
        try {
            if (table.useTable(getArg(1))) {
                System.out.println("using " + getArg(1));
            } else {
                System.out.println(getArg(1) + " not exists");
            }
        } catch (IOException e) {
            throw new MultiDataBaseException(e.getMessage());
        }
    }
}
