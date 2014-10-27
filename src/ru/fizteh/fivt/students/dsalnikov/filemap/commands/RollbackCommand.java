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
        db.rollback();
    }

    @Override
    public String getName() {
        return "rollback";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
