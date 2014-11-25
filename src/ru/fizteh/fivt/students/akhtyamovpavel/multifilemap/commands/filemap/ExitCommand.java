package ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.DataBaseShell;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class ExitCommand implements Command {
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
