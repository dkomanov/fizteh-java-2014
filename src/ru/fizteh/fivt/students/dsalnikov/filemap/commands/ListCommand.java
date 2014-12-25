package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;

import java.io.InputStream;
import java.io.PrintStream;


public class ListCommand extends AbstractCommand {

    private Table db;

    public ListCommand(Table t) {
        super("list", 0);
        db = t;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        for (String s : db.list()) {
            outputStream.println(String.format("%s\n", s));
        }
    }
}
