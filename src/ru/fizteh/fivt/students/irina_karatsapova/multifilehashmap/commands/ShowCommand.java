package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.DataBase;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.Utils;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.LoadTable;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.SaveTable;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.Table;

import java.io.File;

public class ShowCommand implements Command {
    public void execute(String[] args) throws Exception {
        if (!args[1].equals("tables")) {
            throw new Exception("The name of this command is \"show tables\"");
        }
        for (File file: DataBase.root.listFiles()) {
            String tableName = file.getName().toString();
            int valuesNumber;
            if (DataBase.tables.containsKey(tableName)) {
                valuesNumber = DataBase.tables.get(tableName).intValue();
            } else {
                System.out.println("We don't know this table o_O: " + tableName);
                SaveTable.start();
                LoadTable.start(Utils.makePathAbsolute(tableName));
                valuesNumber = Table.countValues();
                DataBase.initInf(tableName, valuesNumber);
            }
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
