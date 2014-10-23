package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.DataBase;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.utils.Utils;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.Table;

public class RemoveCommand implements Command {
    public void execute(String[] args) {
        if (!Utils.checkTableLoading()) {
            return;
        }
        String key = args[1];
        if (!Table.map.containsKey(key)) {
            System.out.println("not found");
        } else {
            Table.map.remove(key);
            DataBase.decreaseValuesNumber(Table.dir.getName());
            System.out.println("removed");
        }
    }

    public String name() {
        return "remove";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
