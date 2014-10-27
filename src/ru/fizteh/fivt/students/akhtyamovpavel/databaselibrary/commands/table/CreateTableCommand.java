package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.fileshell.MakeDirectoryCommand;

import java.io.FileNotFoundException;
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
        if (!onExistCheck(arguments.get(0), NON_EXIST)) {
            return null;
        }
        try {
            new MakeDirectoryCommand(getShell()).executeCommand(arguments);
            shell.insertTable(arguments.get(0));
            System.out.println("created");
        } catch (Exception e) {
            throw new FileNotFoundException();
        }

        return null;

    }

    @Override
    public String getName() {
        return "create";
    }
}
