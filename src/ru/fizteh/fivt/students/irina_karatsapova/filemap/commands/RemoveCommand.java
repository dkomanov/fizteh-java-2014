package ru.fizteh.fivt.students.irina_karatsapova.filemap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.filemap.database.*;

public class RemoveCommand implements Command {
    public void execute(String[] args) {
        String key = args[1];
        if (!DataBase.map.containsKey(key)) {
            System.out.println("not found");
        } else {
            DataBase.map.remove(key);
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
