package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.table;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.fileshell.MakeDirectoryCommand;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
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

        try {
            Table table = shell.createTable(arguments.get(0));
            if (table == null) {
                return arguments.get(0) + " exists";
            }

            return "created";
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    @Override
    public String getName() {
        return "create";
    }
}
