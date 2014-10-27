package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.fileshell.RemoveCommand;

import java.io.FileNotFoundException;
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
            if (!onExistCheck(arguments.get(0), EXIST)) {
                return null;
            }
        } catch (Exception e) {
            throw new FileNotFoundException();
        }

        ArrayList<String> argumentsForRemove = new ArrayList<String>();
        argumentsForRemove.add("-r");
        argumentsForRemove.add(arguments.get(0));
        new RemoveCommand(getShell().getDataBaseDirectory()).executeCommand(argumentsForRemove);
        System.out.println("dropped");
        shell.removeTableFile(arguments.get(0));
        if (arguments.get(0).equals(shell.getOpenedTableName())) {
            shell.setOpenedTableName(null);
            shell.setFileMap(null);
        }
        return null;
    }

    @Override
    public String getName() {
        return "drop";
    }
}
