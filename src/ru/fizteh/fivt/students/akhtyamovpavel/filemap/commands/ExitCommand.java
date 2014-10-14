package ru.fizteh.fivt.students.akhtyamovpavel.filemap.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.filemap.DataBaseShell;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class ExitCommand implements Command{
    DataBaseShell shell;

    public ExitCommand(DataBaseShell link) {
        shell = link;
    }
    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        shell.close();
        System.exit(0);
    }

    @Override
    public String getName() {
        return "exit";
    }
}
