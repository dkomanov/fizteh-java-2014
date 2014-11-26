package ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.DataBaseShell;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.Command;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by user1 on 17.10.2014.
 */
public class ShowTablesCommand extends TableCommand implements Command {
    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage: show tables");
        }
        if (!"tables".equals(arguments.get(0))) {
            throw new Exception("usage: show tables");
        }
        for (Map.Entry<String, Integer> entry: shell.getTableSet().entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    public ShowTablesCommand(DataBaseShell shell) {
        super(shell);
    }

    @Override
    public String getName() {
        return "show";
    }
}
