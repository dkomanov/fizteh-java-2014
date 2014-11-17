package ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.commands.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class PutCommand implements Command {
    private DataBaseTableProvider shell;

    public PutCommand(DataBaseTableProvider shell) {
        this.shell = shell;
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 2) {
            throw new Exception("usage: put key value");
        }
        if (shell.getOpenedTable() == null) {
            return "no table";
        }
        if (!shell.getOpenedTable().containsKey(arguments.get(0))) {
            shell.putKeyToTable(shell.getOpenedTableName());
            Storeable result = shell.getOpenedTable().put(arguments.get(0),
                    shell.deserialize(shell.getOpenedTable(), arguments.get(1)));
            return "new";
        } else {
            Storeable result = shell.getOpenedTable().put(arguments.get(0),
                    shell.deserialize(shell.getOpenedTable(), arguments.get(1)));
            return "overwrite\n" + shell.serialize(shell.getOpenedTable(), result);
        }
    }

    @Override
    public String getName() {
        return "put";
    }
}
