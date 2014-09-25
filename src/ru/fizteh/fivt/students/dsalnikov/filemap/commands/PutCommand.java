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
        if (args.length != 3) {
            throw new IllegalArgumentException("wrong amount of arguments");
        } else {
            String rv = db.put(args[1], args[2]);
            if (rv == null) {
                System.out.println("new");
            } else {
                System.out.println(String.format("overwrite\n'%s'", rv));
            }
        }
    }

    @Override
    public String getName() {
        return "put";
    }

    @Override
    public int getArgsCount() {
        return 3;
    }
}
