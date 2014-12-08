package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.RemoteCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by user1 on 21.10.2014.
 */
public class RollbackCommand extends RemoteCommand {

    private RemoteDataBaseTableProvider table;

    public RollbackCommand(RemoteDataBaseTableProvider table) {
        super(table);
        this.table = table;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (table.isGuested()) {
            return sendCommand(arguments);
        }
        if (!arguments.isEmpty()) {
            throw new Exception("usage rollback");
        }
        if (table.getOpenedTable() == null) {
            return "no table";
        }

        try {
            return Integer.toString(table.getOpenedTable().rollback());
        } catch (Exception e) {

            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "rollback";
    }
}
