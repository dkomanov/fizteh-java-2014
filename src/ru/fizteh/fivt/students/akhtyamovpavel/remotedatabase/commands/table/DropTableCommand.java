package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 07.10.2014.
 */

public class DropTableCommand extends TableCommand implements Command {

    public DropTableCommand(RemoteDataBaseTableProvider shell) {
        super(shell);
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage drop tablename");
        }
        try {
            shell.removeTable(arguments.get(0));
            return "dropped";
        } catch (IllegalStateException e) {
            return e.getMessage();
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "drop";
    }
}
