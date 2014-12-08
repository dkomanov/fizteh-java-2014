package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 21.10.2014.
 */
public class CommitCommand implements Command {
    private DataBaseTableProvider table;

    public CommitCommand(DataBaseTableProvider table) {
        this.table = table;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (!arguments.isEmpty()) {
            throw new Exception("usage: commit");
        }

        if (table.getOpenedTable() == null) {
            return "no table";
        }

        try {
            return Integer.toString(table.getOpenedTable().commit());
        } catch (Exception e) {
            throw new Exception("commit error");
        }
    }

    @Override
    public String getName() {
        return "commit";
    }
}
