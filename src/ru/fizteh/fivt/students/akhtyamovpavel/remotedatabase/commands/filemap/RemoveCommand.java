package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class RemoveCommand implements Command {
    private RemoteDataBaseTableProvider shell;

    public RemoveCommand(RemoteDataBaseTableProvider shell) {
        this.shell = shell;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage: remove key");
        }

        if (shell.getOpenedTable() == null) {
            return "no table";
        }
        if (shell.getOpenedTable().containsKey(arguments.get(0))) {
            shell.getOpenedTable().remove(arguments.get(0));
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
