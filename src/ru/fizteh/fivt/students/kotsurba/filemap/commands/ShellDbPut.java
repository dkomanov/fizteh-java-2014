package ru.fizteh.fivt.students.kotsurba.filemap.commands;

import ru.fizteh.fivt.students.kotsurba.filemap.database.DataBase;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.CommandString;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public final class ShellDbPut extends SimpleShellCommand {
    private DataBase dataBase;

    public ShellDbPut(final DataBase newDataBase) {
        setName("put");
        setNumberOfArgs(3);
        setHint("usage: put <key> <value>");
        dataBase = newDataBase;
    }

    @Override
    public void run() {
        String str = dataBase.put(getArg(1), getSpacedArg(2));
        if (str == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(str);
        }
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
