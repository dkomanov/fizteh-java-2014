package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.InputStream;
import java.io.PrintStream;


public class ListCommand implements Command {

    private Table db;

    public ListCommand(Table t) {
        db = t;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        for (String s : db.list()) {
            outputStream.println(String.format("%s\n", s));
        }
    }


    @Override
    public String getName() {
        return "list";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
