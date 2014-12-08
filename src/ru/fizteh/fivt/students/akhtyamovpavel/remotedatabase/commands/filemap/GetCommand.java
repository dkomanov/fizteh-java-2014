package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.filemap;


import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class GetCommand implements Command {
    private RemoteDataBaseTableProvider shell;

    public GetCommand(RemoteDataBaseTableProvider shell) {
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
            return "found\n" + shell.serialize(shell.getOpenedTable(), shell.getOpenedTable().get(arguments.get(0)));
        } else {
            return "not found";
        }
    }

    @Override
    public String getName() {
        return "get";
    }
}
