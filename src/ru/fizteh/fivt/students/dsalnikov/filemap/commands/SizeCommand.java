package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

public class SizeCommand implements Command {

    Table db;

    public SizeCommand(Table t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("wrong amount of arguments");
        } else {
            System.out.println(db.size());
        }
    }

    @Override
    public String getName() {
        return "size";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
