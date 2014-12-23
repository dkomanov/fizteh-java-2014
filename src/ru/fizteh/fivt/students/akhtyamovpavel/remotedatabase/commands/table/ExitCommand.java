package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class ExitCommand implements Command {
    RemoteDataBaseTableProvider table;

    public ExitCommand(RemoteDataBaseTableProvider link) {
        table = link;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (table.isGuested()) {
            return table.disconnect();
        } else {
            table.close();
            System.exit(0);
        }
        return null;
    }

    @Override
    public String getName() {
        return "exit";
    }
}
