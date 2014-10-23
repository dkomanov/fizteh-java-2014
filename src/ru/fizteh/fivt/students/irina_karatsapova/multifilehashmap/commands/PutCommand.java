package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.DataBase;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.utils.Utils;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.Table;

public class PutCommand implements Command {
    public void execute(String[] args) {
        if (!Utils.checkTableLoading()) {
            return;
        }
        String key = args[1];
        String value = args[2];
        if (!Table.map.containsKey(key)) {
            Table.addKey(key);
            DataBase.increaseValuesNumber(Table.dir.getName());
            System.out.println("new");
        } else {
            System.out.println("owerwrite");
            System.out.println(Table.map.get(key));
        }
        Table.map.put(key, value);
    }
    
    public String name() {
        return "put";
    }
    
    public int minArgs() {
        return 3;
    }
    
    public int maxArgs() {
        return 3;
    }
}
