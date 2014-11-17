package ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.commands.Command;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by user1 on 17.10.2014.
 */
public class ShowTablesCommand extends TableCommand implements Command {
    public ShowTablesCommand(DataBaseTableProvider shell) {
        super(shell);
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage: show tables");
        }
        if (!"tables".equals(arguments.get(0))) {
            throw new Exception("usage: show tables");
        }
        for (Map.Entry<String, Integer> entry : shell.getTableSet().entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        return null;
    }

    @Override
    public String getName() {
        return "show";
    }
}
