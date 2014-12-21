package ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.commands.filemap;

import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class ListCommand implements Command {
    private DataBaseTableProvider shell;

    public ListCommand(DataBaseTableProvider shell) {
        this.shell = shell;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {

        if (!arguments.isEmpty()) {
            throw new Exception("usage: list");
        }
        if (shell.getOpenedTable() == null) {
            return "no table";
        }
        return String.join(",", shell.getOpenedTable().list());
    }

    @Override
    public String getName() {
        return "list";
    }
}
