package ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.DB;

public class PutCmd implements Cmd {
    public final String getName() {
        return "put";
    }

    public final void execute(final DB dataBase, final String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        if (dataBase.getBase().get(args[1]) != null) {
            System.out.println("overwrite");
            System.out.println(args[2]);
        } else {
            System.out.println("new");
        }
        dataBase.putBase(args[1], args[2]);
    }
}
