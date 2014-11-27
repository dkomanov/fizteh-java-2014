package ru.fizteh.fivt.students.irina_karatsapova.junit.commands;

import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.TableException;

import java.util.Set;

public class ShowCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) throws Exception {
        if (!args[1].equals("tables")) {
            throw new Exception("The name of this command is \"show tables\"");
        }
        Set<String> tableNames = tableProvider.tableNames();
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
