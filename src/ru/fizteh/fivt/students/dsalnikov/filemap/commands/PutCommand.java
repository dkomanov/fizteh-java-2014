package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;

import java.io.InputStream;
import java.io.PrintStream;

public class PutCommand extends AbstractCommand {

    private Table db;

    public PutCommand(Table t) {
        super("put", 2);
        db = t;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        String result = db.put(args[1], args[2]);
        if (result == null) {
            outputStream.println("new");
        } else {
            outputStream.println(String.format("overwrite\n'%s'", result));
        }

    }
}
