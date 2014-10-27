package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.DataBaseTable;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 21.10.2014.
 */
public class RollbackCommand implements Command {

    private DataBaseTable table;

    public RollbackCommand(DataBaseTable table) {
        this.table = table;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (!arguments.isEmpty()) {
            throw new Exception("usage rollback");
        }

        try {
            return Integer.toString(table.rollback()) + " unsaved changes";
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "rollback";
    }
}
