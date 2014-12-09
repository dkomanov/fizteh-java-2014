package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.InputStream;
import java.io.PrintStream;

public class SizeCommand implements Command {

    Table db;

    public SizeCommand(Table t) {
        db = t;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        outputStream.println(db.size());
    }

    @Override
    public String getName() {
        return "size";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
