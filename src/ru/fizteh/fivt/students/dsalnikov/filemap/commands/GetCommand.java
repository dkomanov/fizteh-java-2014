package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

/**
 * Created by Dmitriy on 9/24/2014.
 */
public class GetCommand implements Command {
    @Override
    public void execute(String[] args) throws Exception {

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
