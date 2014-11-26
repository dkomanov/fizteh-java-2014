package ru.fizteh.fivt.students.kotsurba.filemap.commands;

import ru.fizteh.fivt.students.kotsurba.filemap.database.DataBase;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.CommandString;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public class ShellDbList extends SimpleShellCommand {
    private DataBase dataBase;

    public ShellDbList(final DataBase newDataBase) {
        setName("list");
        setNumberOfArgs(1);
        setHint("usage: list");
        dataBase = newDataBase;
    }

    @Override
    public void run() {
        String str = dataBase.list();
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
