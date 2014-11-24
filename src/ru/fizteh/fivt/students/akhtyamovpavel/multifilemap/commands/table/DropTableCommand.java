package ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.DataBaseShell;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.fileshell.RemoveCommand;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 07.10.2014.
 */

public class DropTableCommand extends TableCommand implements Command {

    public DropTableCommand(DataBaseShell shell) {
        super(shell);
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage drop tablename");
        }

        if (!onExistCheck(arguments.get(0), EXIST)) {
            return;
        }
        ArrayList<String> argumentsForRemove = new ArrayList<String>();
        argumentsForRemove.add("-r");
        argumentsForRemove.add(arguments.get(0));
        new RemoveCommand(getShell().getDataBaseDirectory()).executeCommand(argumentsForRemove);
        System.out.println("dropped");
        shell.removeTable(arguments.get(0));
        if (arguments.get(0).equals(shell.getOpenedTableName())) {
            shell.setOpenedTableName(null);
            shell.setFileMap(null);

        }
    }

    @Override
    public String getName() {
        return "drop";
    }
}
