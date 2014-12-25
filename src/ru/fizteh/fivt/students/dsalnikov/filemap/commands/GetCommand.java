package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;

import java.io.InputStream;
import java.io.PrintStream;

public class GetCommand extends AbstractCommand {

    private Table db;

    public GetCommand(Table t) {
        super("get", 1);
        db = t;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        String result = db.get(args[1]);
        if (result == null) {
            outputStream.println("not found");
        } else {
            outputStream.println(String.format("found\n'%s'", result));
        }
    }
}
