package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.InputStream;
import java.io.PrintStream;

public class ExitCommand implements Command {

    private Table db;

    public ExitCommand(Table table) {
        db = table;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        db.exit();
        System.exit(0);
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
