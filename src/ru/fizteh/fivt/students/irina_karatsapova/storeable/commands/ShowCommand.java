package ru.fizteh.fivt.students.irina_karatsapova.storeable.commands;

import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProvider;

import java.util.List;

public class ShowCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) throws Exception {
        if (!args[1].equals("tables")) {
            throw new Exception("The name of this command is \"show tables\"");
        }
        List<String> tableNames = tableProvider.getTableNames();
        for (String tableName: tableNames) {
            int valuesNumber = tableProvider.getTable(tableName).size();
            System.out.println(tableName + " " + valuesNumber);
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
