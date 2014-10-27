package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.filemap;


import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.DataBaseTable;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class GetCommand implements Command {
    private DataBaseTable shell;

    public GetCommand(DataBaseTable shell) {
        this.shell = shell;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage: get key");
        }
        if (shell == null) {
            return "no table";
        }
        if (shell.containsKey(arguments.get(0))) {
            return "found\n" + shell.get(arguments.get(0));
        } else {
            return "not found";
        }
    }

    @Override
    public String getName() {
        return "get";
    }
}
