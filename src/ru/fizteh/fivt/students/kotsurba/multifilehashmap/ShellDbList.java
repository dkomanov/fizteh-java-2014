package ru.fizteh.fivt.students.kotsurba.multifilehashmap;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.CommandString;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public class ShellDbList extends SimpleShellCommand {
    private DataBaseTable dataBaseTable;

    public ShellDbList(final DataBaseTable newDataBaseTable) {
        setName("list");
        setNumberOfArgs(1);
        setHint("usage: list");
        dataBaseTable = newDataBaseTable;
    }

    @Override
    public void run() {
        if (!dataBaseTable.exist()) {
            System.out.println("no table");
            return;
        }
        String str = dataBaseTable.list();
        System.out.println(str);
    }

    @Override
    public boolean isMyCommand(final CommandString command) {
        if (name.equals(command.getArg(0))) {
            if (command.length() < numberOfArgs) {
                throw new InvalidCommandException(name + " " + hint);
            }
            args = command;
            return true;
        }
        return false;
    }
}
