package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 14.10.2014.
 */
public class UseCommand extends TableCommand implements Command{
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

        if (!onExistCheck(arguments.get(0), EXIST)) {
            return null;
        }
        if (shell.getOpenedTable() != null) {
            if (shell.getOpenedTableName().equals(arguments.get(0))) {
                return "using" + arguments.get(0);
            } else {

                if (shell.getOpenedTable().hasUnsavedChanges()) {
                    throw new Exception(Integer.toString(shell.getOpenedTable()
                            .getNumberOfChanges()) + " unsaved changes");
                }
            }
        }

        try {
            shell.getTable(arguments.get(0));
            shell.refreshCommands();
            if (!isSilent) {

                return "using " + arguments.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new Exception("changing table error");
        }

    }

    @Override
    public String getName() {
        return "use";
    }
}
