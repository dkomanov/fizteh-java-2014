package ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.DB;

public class RemoveCmd extends Cmd {
    public final String getName() {
        return "remove";
    }

    public final void execute(
            final DB dataBase, final String[] args) {
        checkArgs(2, args);
        if (dataBase.getBase().get(args[1]) != null) {
            dataBase.removeBase(args[1]);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}
