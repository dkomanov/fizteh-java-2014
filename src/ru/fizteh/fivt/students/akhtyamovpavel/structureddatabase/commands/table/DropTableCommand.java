package ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.commands.Command;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 07.10.2014.
 */

public class DropTableCommand extends TableCommand implements Command {

    public DropTableCommand(DataBaseTableProvider shell) {
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
