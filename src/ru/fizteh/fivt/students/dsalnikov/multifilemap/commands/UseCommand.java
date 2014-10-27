package ru.fizteh.fivt.students.dsalnikov.multifilemap.commands;

import ru.fizteh.fivt.students.dsalnikov.multifilemap.MultiTable;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

public class UseCommand implements Command {

    MultiTable db;

    public UseCommand(MultiTable t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {
        db.use(args[1]);
    }

    @Override
    public String getName() {
        return "use";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
