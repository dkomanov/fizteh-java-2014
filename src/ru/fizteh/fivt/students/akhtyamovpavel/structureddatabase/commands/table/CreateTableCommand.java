package ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.commands.table;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 07.10.2014.
 */
public class CreateTableCommand extends TableCommand implements Command {


    public CreateTableCommand(DataBaseTableProvider shell) {
        super(shell);
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage create tablename");
        }

        try {
            Table table = shell.createTable(arguments.get(0), null);
            if (table == null) {
                return arguments.get(0) + " exists";
            }

            return "created";
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    @Override
    public String getName() {
        return "create";
    }
}
