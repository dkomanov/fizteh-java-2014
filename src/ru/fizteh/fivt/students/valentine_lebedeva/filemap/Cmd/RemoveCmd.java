package ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.DB;

public class RemoveCmd implements Cmd {
    public final String getName() {
        return "remove";
    }

    public final void execute(final DB dataBase, final String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        if (dataBase.getBase().get(args[1]) != null) {
            dataBase.removeBase(args[1]);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}
