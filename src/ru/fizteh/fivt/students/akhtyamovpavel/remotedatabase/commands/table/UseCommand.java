package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.table;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 14.10.2014.
 */
public class UseCommand extends TableCommand implements Command {
    private boolean isSilent;

    public UseCommand(DataBaseTableProvider shell, boolean isSilent) {
        super(shell);
        this.isSilent = isSilent;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {

        if (arguments.size() != 1) {
            throw new Exception("usage: use tableName");
        }


        try {
            Table table = shell.getTable(arguments.get(0));
            if (!isSilent) {
                if (table == null) {
                    return arguments.get(0) + " not exists";
                } else {
                    return "using " + arguments.get(0);
                }
            } else {
                return null;
            }
        } catch (IllegalArgumentException iae) {
            throw new Exception(iae.getMessage());
        }

    }

    @Override
    public String getName() {
        return "use";
    }
}
