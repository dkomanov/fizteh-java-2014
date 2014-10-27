package ru.fizteh.fivt.students.dsalnikov.multifilemap.commands;

import ru.fizteh.fivt.students.dsalnikov.multifilemap.MultiTable;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

public class ShowCommand implements Command {

    MultiTable db;

    public ShowCommand(MultiTable t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (!args[1].equals("tables")) {
            throw new IllegalArgumentException("wrong command: use show tables");
        } else {
            db.showTables();
        }
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
