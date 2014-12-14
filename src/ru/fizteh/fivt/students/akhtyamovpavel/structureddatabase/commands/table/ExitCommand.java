package ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class ExitCommand implements Command {
    DataBaseTableProvider table;

    public ExitCommand(DataBaseTableProvider link) {
        table = link;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        table.close();
        System.exit(0);
        return null;
    }

    @Override
    public String getName() {
        return "exit";
    }
}
