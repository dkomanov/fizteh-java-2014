package ru.fizteh.fivt.students.irina_karatsapova.filemap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.filemap.database.*;

public class GetCommand implements Command {
    public void execute(String[] args) {
        String key = args[1];
        if (!DataBase.map.containsKey(key)) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(DataBase.map.get(key));
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
