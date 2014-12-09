package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.InputStream;
import java.io.PrintStream;

public class RollbackCommand implements Command {

    Table db;

    public RollbackCommand(Table t) {
        db = t;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
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
