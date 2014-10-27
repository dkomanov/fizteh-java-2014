package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

public class PutCommand implements Command {

    private Table db;

    public PutCommand(Table t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {
        String result = db.put(args[1], args[2]);
        if (result == null) {
            System.out.println("new");
        } else {
            System.out.println(String.format("overwrite\n'%s'", result));
        }

    }

    @Override
    public String getName() {
        return "put";
    }

    @Override
    public int getArgsCount() {
        return 2;
    }
}
