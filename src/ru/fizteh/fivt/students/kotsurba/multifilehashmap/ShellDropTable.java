package ru.fizteh.fivt.students.kotsurba.multifilehashmap;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

import java.io.IOException;

public class ShellDropTable extends SimpleShellCommand {
    private DataBaseTable table;

    public ShellDropTable(DataBaseTable newTable) {
        table = newTable;
        setName("drop");
        setNumberOfArgs(2);
        setHint("usage: drop <table name>");
    }

    public void run() {
        try {
            if (table.dropTable(getArg(1))) {
                System.out.println("dropped");
            } else {
                System.out.println(getArg(1) + " not exists");
            }
        } catch (IOException e) {
            throw new MultiDataBaseException(e.getMessage());
        }
    }
}

