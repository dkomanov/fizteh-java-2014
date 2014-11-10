package ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.commands.filemap;


import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class GetCommand implements Command {
    private DataBaseTableProvider shell;

    public GetCommand(DataBaseTableProvider shell) {
        this.shell = shell;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage: get key");
        }
        if (shell.getOpenedTable() == null) {
            return "no table";
        }
        if (shell.getOpenedTable().containsKey(arguments.get(0))) {
            return "found\n" + shell.getOpenedTable().get(arguments.get(0));
        } else {
            return "not found";
        }
    }

    @Override
    public String getName() {
        return "get";
    }
}
