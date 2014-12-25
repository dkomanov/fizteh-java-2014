package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;

import java.io.InputStream;
import java.io.PrintStream;

public class RemoveCommand extends AbstractCommand {

    private Table db;

    public RemoveCommand(Table t) {
        super("remove", 1);
        db = t;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        String result = db.remove(args[1]);
        if (result == null) {
            outputStream.println("not found");
        } else {
            outputStream.println("removed");
        }

    }
}
