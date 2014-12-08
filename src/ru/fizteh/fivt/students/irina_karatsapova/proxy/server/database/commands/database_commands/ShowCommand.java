package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.database_commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.InterpreterStateDatabase;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands.DatabaseCommand;

import java.util.List;

public class ShowCommand implements DatabaseCommand {
    public void execute(InterpreterStateDatabase state, String[] args) throws Exception {
        if (!args[1].equals("tables")) {
            throw new Exception("The name of this command is \"show tables\"");
        }
        List<String> tableNames = state.tableProvider.getTableNames();
        for (String tableName: tableNames) {
            int valuesNumber = state.tableProvider.getTable(tableName).size();
            state.out.println(tableName + " " + valuesNumber);
        }
    }

    public String name() {
        return "show";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
