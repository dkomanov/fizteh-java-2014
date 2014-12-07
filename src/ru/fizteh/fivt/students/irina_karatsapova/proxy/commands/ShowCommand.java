package ru.fizteh.fivt.students.irina_karatsapova.proxy.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.InterpreterState;

import java.util.List;

public class ShowCommand implements Command {
    public void execute(InterpreterState state, String[] args) throws Exception {
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
