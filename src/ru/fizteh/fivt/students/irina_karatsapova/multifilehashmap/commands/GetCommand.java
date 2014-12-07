package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.utils.Utils;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.Table;

public class GetCommand implements Command {
    public void execute(String[] args) {
        if (!Utils.checkTableLoading()) {
            return;
        }
        String key = args[1];
        if (!Table.map.containsKey(key)) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(Table.map.get(key));
        }
    }

    public String name() {
        return "get";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
