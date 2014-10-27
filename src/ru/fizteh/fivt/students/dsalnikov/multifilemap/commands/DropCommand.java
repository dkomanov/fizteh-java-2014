package ru.fizteh.fivt.students.dsalnikov.multifilemap.commands;

import ru.fizteh.fivt.students.dsalnikov.multifilemap.MultiTable;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

public class DropCommand implements Command {

    MultiTable db;

    public DropCommand(MultiTable t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {
        db.drop(args[1]);
    }

    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
