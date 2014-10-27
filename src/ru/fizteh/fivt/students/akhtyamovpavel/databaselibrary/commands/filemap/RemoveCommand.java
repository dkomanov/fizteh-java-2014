package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.DataBaseTable;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class RemoveCommand implements Command {
    private DataBaseTable shell;

    public RemoveCommand(DataBaseTable shell) {
        this.shell = shell;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage: remove key");
        }

        if (shell == null) {
            return "no table";
        }
        if (shell.containsKey(arguments.get(0))) {
            shell.remove(arguments.get(0));
            return "removed";
        } else {
            return "not found";
        }
    }

    @Override
    public String getName() {
        return "remove";
    }
}
