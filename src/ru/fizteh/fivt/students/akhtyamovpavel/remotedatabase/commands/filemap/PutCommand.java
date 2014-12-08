package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class PutCommand implements Command {
    private RemoteDataBaseTableProvider shell;

    public PutCommand(RemoteDataBaseTableProvider shell) {
        this.shell = shell;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 2) {
            throw new Exception("usage: put key value");
        }
        if (shell.getOpenedTable() == null) {
            return "no table";
        }
        Storeable result = shell.getOpenedTable().put(arguments.get(0),
                shell.deserialize(shell.getOpenedTable(), arguments.get(1)));
        if (result == null) {
            return "new";
        } else {
            return "overwrite\n" + shell.serialize(shell.getOpenedTable(), result);
        }
    }

    @Override
    public String getName() {
        return "put";
    }
}
