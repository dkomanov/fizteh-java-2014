package ru.fizteh.fivt.students.irina_karatsapova.filemap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.filemap.database.*;

public class PutCommand implements Command {
    public void execute(String[] args) {
        String key = args[1];
        String value = args[2];
        if (!DataBase.map.containsKey(key)) {
            System.out.println("new");
            DataBase.keys.add(key);
        } else {
            System.out.println("owerwrite");
            System.out.println(DataBase.map.get(key));
        }
        DataBase.map.put(key, value);

        if (DataBase.map.isEmpty()) {
            System.out.println("Empty!");
        }
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
