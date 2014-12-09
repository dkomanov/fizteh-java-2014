package ru.fizteh.fivt.students.dsalnikov.multifilemap.commands;


import ru.fizteh.fivt.students.dsalnikov.multifilemap.MultiFileHashMap;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.MultiTable;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;


public class CreateCommand implements Command {

    MultiTable db;

    public CreateCommand(MultiTable t) {
        db = t;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        File tableDir = new File(db.getDbPath(), args[1]);
        if (tableDir.exists()) {
            outputStream.println(args[1] + " exists");
        } else {
            db.create(Arrays.asList(args));
            outputStream.println("created");
        }
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public int getArgsCount() {
        if (db instanceof MultiFileHashMap) {
            return 1;
        } else {
            return 2;
        }
    }
}
