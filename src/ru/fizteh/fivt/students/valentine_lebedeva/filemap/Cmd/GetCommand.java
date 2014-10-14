package ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.DB;

public class GetCommand extends Command {
    public final void execute(final DB dataBase, final String[] args) {
        checkArgs(2, args);
        if (dataBase.getBase().get(args[1]) != null) {
            System.out.println("found");
            System.out.println(dataBase.getBase().get(args[1]));
        } else {
            System.out.println("not found");
        }
    }
}
