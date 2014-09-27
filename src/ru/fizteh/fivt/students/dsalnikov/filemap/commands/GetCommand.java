package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

public class GetCommand implements Command {

    private Table db;

    public GetCommand(Table t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("wrong amount of arguments");
        } else {
            String result = db.get(args[1]);
            if (result == null) {
                System.out.println("not found");
            } else {
                System.out.println(String.format("found\n'%s'", result));
            }
        }
    }

    @Override
    public String getName() {
        return "get";
    }

    @Override
    public int getArgsCount() {
        return 2;
    }
}
