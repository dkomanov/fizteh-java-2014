package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.utils.Utils;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.LoadTable;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.SaveTable;

import java.io.File;

public class UseCommand implements Command {
    public void execute(String[] args) throws Exception {
        SaveTable.start();
        String tableName = args[1];
        File usingTable = Utils.makePathAbsolute(tableName);
        if (usingTable.exists()) {
            LoadTable.start(usingTable);
            System.out.println("using " + tableName);
        } else {
            System.out.println(tableName + " not exists");
        }
    }

    public String name() {
        return "use";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
