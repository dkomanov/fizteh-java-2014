package ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.DataBaseShell;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.fileshell.MakeDirectoryCommand;

import java.util.ArrayList;

/**
 * Created by user1 on 07.10.2014.
 */
public class CreateTableCommand extends TableCommand implements Command {


    public CreateTableCommand(DataBaseShell shell) {
        super(shell);
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage create tablename");
        }
        if (!onExistCheck(arguments.get(0), NON_EXIST)) {
            return;
        }

        new MakeDirectoryCommand(getShell()).executeCommand(arguments);
        shell.insertTable(arguments.get(0));
        System.out.println("created");

    }

    @Override
    public String getName() {
        return "create";
    }
}
