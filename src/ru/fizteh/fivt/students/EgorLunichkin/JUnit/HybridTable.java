package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class HybridTable {
    public HybridTable(Table usualTable) {
        cleanTable = usualTable;
        dirtyTable = new DirtyTable();
        commands = new ArrayList<Command>();
        rollback();
    }

    private Table cleanTable;
    private DirtyTable dirtyTable;
    private ArrayList<Command> commands;

    public int commit() {
        int diff = diffTables(cleanTable, dirtyTable);
        for (Command command : commands) {
            command.run();
        }
        commands.clear();
        return diff;
    }

    public int rollback() {
        int diff = diffTables(cleanTable, dirtyTable);
        dirtyTable.importData(cleanTable);
        commands.clear();
        return diff;
    }

    private static int diffTables(Table first, Table second) {
        int diff = 0;
        for (int dir = 0; dir < 16; ++dir) {
            for (int file = 0; file < 16; ++file) {
                if (first.dataBases[dir][file] == null && second.dataBases[dir][file] != null) {
                    diff += second.dataBases[dir][file].getDataBase().size();
                } else if (first.dataBases[dir][file] != null && second.dataBases[dir][file] == null) {
                    diff += first.dataBases[dir][file].getDataBase().size();
                } else if (first.dataBases[dir][file] != null && second.dataBases[dir][file] != null) {
                    diff += diffHashMaps(first.dataBases[dir][file].getDataBase(), second.dataBases[dir][file].getDataBase());
                }
            }
        }
        return diff;
    }

    private static int diffHashMaps(HashMap<String, String> first, HashMap<String, String> second) {
        int diff = 0;
        for (HashMap.Entry<String, String> entry : first.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (second.containsKey(key) && !second.get(key).equals(value)) {
                ++diff;
            }
        }
        HashSet<String> intersection = new HashSet<String>(first.keySet());
        intersection.retainAll(second.keySet());
        diff += first.size() + second.size() - 2 * intersection.size();
        return diff;
    }
}
