package ru.fizteh.fivt.students.kotsurba.multifilehashmap;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

import java.io.IOException;

public class ShellShowTables extends SimpleShellCommand {
    private DataBaseTable dataBase;

    public ShellShowTables(final DataBaseTable newBase) {
        setName("show");
        setNumberOfArgs(2);
        setHint("usage: show tables");
        dataBase = newBase;
    }

    @Override
    public void run() {
        if (getArg(1).equals("tables")) {
            try {
                String str = dataBase.show();
                System.out.print(str);
            } catch (IOException e) {
                throw new InvalidCommandException(e.getMessage());
            }

        } else {
            System.out.println("Invalid command!");
        }
    }
}
