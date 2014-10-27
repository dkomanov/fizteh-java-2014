package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.DataBaseTable;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 21.10.2014.
 */
public class CommitCommand implements Command {
    private DataBaseTable table;

    public CommitCommand(DataBaseTable table) {
        this.table = table;
    }
    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (!arguments.isEmpty()) {
            throw new Exception("usage: commit");
        }

        try {
            return Integer.toString(table.commit());
        } catch (Exception e) {
            throw new Exception("commit error");
        }
    }

    @Override
    public String getName() {
        return "commit";
    }
}
