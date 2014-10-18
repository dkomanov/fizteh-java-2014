package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

public class RollbackCommand implements Command {

    Table db;

    public RollbackCommand(Table t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("wrong amout of arguments");
        } else {
            db.rollback();
        }
    }

    @Override
    public String getName() {
        return "rollback";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
