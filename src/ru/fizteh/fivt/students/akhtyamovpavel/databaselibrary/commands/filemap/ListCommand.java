package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.DataBaseTable;
import ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class ListCommand implements Command {
    private DataBaseTable shell;

    public ListCommand(DataBaseTable shell) {
        this.shell = shell;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {

        if (!arguments.isEmpty()) {
            throw new Exception("usage: list");
        }
        if (shell == null) {
            return "no table";
        }
        return String.join(",", shell.list());
    }

    @Override
    public String getName() {
        return "list";
    }
}
