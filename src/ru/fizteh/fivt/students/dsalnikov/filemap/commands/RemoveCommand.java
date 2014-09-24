package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

public class RemoveCommand implements Command {
    @Override
    public void execute(String[] args) throws Exception {

    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
