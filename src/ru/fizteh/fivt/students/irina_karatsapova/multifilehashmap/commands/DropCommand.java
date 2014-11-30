package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.DataBase;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.utils.Utils;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.SaveTable;

import java.io.File;

public class DropCommand implements Command {
    public void execute(String[] args) throws Exception {
        SaveTable.start();
        String tableName = args[1];
        File removingTable = Utils.makePathAbsolute(tableName);
        if (removingTable.exists()) {
            Utils.rmdirs(removingTable);
            DataBase.tables.remove(tableName);
            System.out.println("dropped");
        } else {
            System.out.println(tableName + "not exists");
        }
    }

    public String name() {
        return "drop";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
