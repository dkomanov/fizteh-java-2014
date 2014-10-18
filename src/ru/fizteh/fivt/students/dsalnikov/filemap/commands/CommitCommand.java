package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;


public class CommitCommand implements Command {

    private Table db;

    public CommitCommand(Table t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("wrong amount of arguments");
        } else {
            db.commit();
        }
    }

    @Override
    public String getName() {
        return "commit";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
