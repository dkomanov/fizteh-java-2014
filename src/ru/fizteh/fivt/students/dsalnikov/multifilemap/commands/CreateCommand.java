package ru.fizteh.fivt.students.dsalnikov.multifilemap.commands;


import ru.fizteh.fivt.students.dsalnikov.multifilemap.MultiTable;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

public class CreateCommand implements Command {

    MultiTable db;

    public CreateCommand(MultiTable t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {
        db.create(args[1]);
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
