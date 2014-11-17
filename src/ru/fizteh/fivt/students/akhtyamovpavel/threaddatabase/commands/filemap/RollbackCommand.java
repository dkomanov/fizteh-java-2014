package ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 21.10.2014.
 */
public class RollbackCommand implements Command {

    private DataBaseTableProvider table;

    public RollbackCommand(DataBaseTableProvider table) {
        this.table = table;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (!arguments.isEmpty()) {
            throw new Exception("usage rollback");
        }
        if (table.getOpenedTable() == null) {
            return "no table";
        }

        try {
            String result = Integer.toString(table.getOpenedTable().rollback());
            table.rollbackTableSize(table.getOpenedTableName());
            return result;
        } catch (Exception e) {

            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "rollback";
    }
}
