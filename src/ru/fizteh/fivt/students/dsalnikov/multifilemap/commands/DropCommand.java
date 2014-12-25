package ru.fizteh.fivt.students.dsalnikov.multifilemap.commands;

import ru.fizteh.fivt.students.dsalnikov.multifilemap.MultiTable;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;

import java.io.InputStream;
import java.io.PrintStream;

public class DropCommand extends AbstractCommand {

    MultiTable db;

    public DropCommand(MultiTable t) {
        super("drop", 1);
        db = t;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        db.drop(args[1]);
    }
}

