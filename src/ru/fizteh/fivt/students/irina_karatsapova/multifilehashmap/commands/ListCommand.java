package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.Table;

import java.util.List;

public class ListCommand implements Command {
    public void execute(String[] args) throws Exception {
        List<String>[][] keys = Table.keys;
        String allKeys = "";
        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                for (String key: Table.keys[dir][file]) {
                    if (allKeys.length() > 0) {
                        allKeys += ", ";
                    }
                    allKeys += key;
                }
            }
        }
        System.out.println(allKeys);
    }

    public String name() {
        return "list";
    }

    public int minArgs() {
        return 1;
    }

    public int maxArgs() {
        return 1;
    }
}
