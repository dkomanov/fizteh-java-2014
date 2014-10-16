package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.DataBase;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.Utils;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.SaveTable;

import java.io.File;

public class CreateCommand implements Command {
    public void execute(String[] args) throws Exception {
        SaveTable.start();
        String tableName = args[1];
        File newTable = Utils.makePathAbsolute(tableName);
        if (newTable.exists()) {
            System.out.println(tableName + " exists");
        } else {
            newTable.mkdir();
            DataBase.initInf(tableName, 0);
            DataBase.tables.put(tableName, 0);
            System.out.println("created");
        }
    }

    public String name() {
        return "create";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
