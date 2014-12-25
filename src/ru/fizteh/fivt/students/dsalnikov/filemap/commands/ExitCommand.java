package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;

import java.io.InputStream;
import java.io.PrintStream;

public class ExitCommand extends AbstractCommand {

    private Table db;

    public ExitCommand(Table table) {
        super("exit", 0);
        db = table;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        int changesNumber = db.exit();
        if (changesNumber > 0) {
            outputStream.println(String.format("there are %s + uncommitted changes. Do something", changesNumber));
            return;
        }
        System.exit(0);
    }
}
