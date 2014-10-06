package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;


public class ListCommand implements Command {

    private Table db;

    public ListCommand(Table t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("wrong amount of arguments");
        } else {
            db.list();
        }
    }


    @Override
    public String getName() {
        return "list";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
