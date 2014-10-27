package ru.fizteh.fivt.students.dsalnikov.multifilemap.commands;


import ru.fizteh.fivt.students.dsalnikov.multifilemap.MultiTable;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.File;


public class CreateCommand implements Command {

    MultiTable db;

    public CreateCommand(MultiTable t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {
        File tableDir = new File(db.getDbPath(), args[1]);
        if (tableDir.exists()) {
            System.out.println(args[1] + " exists");
        } else {
            db.create(args[1]);
            System.out.println("created");
        }
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
