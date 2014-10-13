package ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.DB;

public class GetCmd implements Cmd {
    public final String getName() {
        return "get";
    }
    public final void execute(final DB dataBase, final String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        if (dataBase.getBase().get(args[1]) != null) {
            System.out.println("found");
            System.out.println(dataBase.getBase().get(args[1]));
        } else {
            System.out.println("not found");
        }
    }
}
