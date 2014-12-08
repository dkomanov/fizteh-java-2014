package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class ListCommand implements Command {
    private RemoteDataBaseTableProvider shell;

    public ListCommand(RemoteDataBaseTableProvider shell) {
        this.shell = shell;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {

        if (!arguments.isEmpty()) {
            throw new Exception("usage: list");
        }
        if (shell.getOpenedTable() == null) {
            return "no table";
        }
        return String.join(",", shell.getOpenedTable().list());
    }

    @Override
    public String getName() {
        return "list";
    }
}
