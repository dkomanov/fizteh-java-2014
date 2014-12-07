package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;


import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.Table;
import java.util.*;

public class HybridTable {
    public Table cleanTable;
    FancyTable dirtyTable;
    ArrayList<Command> changes;

    public HybridTable(Table ordinaryTable) throws Exception {
        cleanTable = ordinaryTable;
        dirtyTable = new FancyTable();
        changes = new ArrayList<>();
        rollBack();
    }

    public int rollBack() {
        int ans = diffTables(cleanTable, dirtyTable);
        dirtyTable.importMap(cleanTable);
        changes.clear();
        return ans;
    }

    public int uncommitedChanges() {
        return diffTables(cleanTable, dirtyTable);
    }

    public int commit() throws Exception {
        int ans = diffTables(cleanTable, dirtyTable);
        for (Command command: changes) {
            command.executeOnTable(cleanTable);
        }
        changes.clear();
        return ans;
    }

    private static int diffTables(Table first, Table second) {
        int result = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (first.databases[i][j] == null && second.databases[i][j] != null) {
                    //кол-во записей ключ/значение в хэшмапе
                    result += second.databases[i][j].data.size();
                } else if (first.databases[i][j] != null && second.databases[i][j] == null) {
                    result += first.databases[i][j].data.size();
                } else if (first.databases[i][j] != null && second.databases[i][j] != null) {
                    result += diffHashMaps(first.databases[i][j].data, second.databases[i][j].data);
                }
            }
        }
        return result;
    }

    private static int diffHashMaps(HashMap<String, String> first, HashMap<String, String> second) {
        int result = 0;
        for (Map.Entry<String, String> entry: first.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            //считает кол-во различных записей ключ/значение (которые нужно изменить)
            if (second.containsKey(key) && !second.get(key).equals(value)) {
                result++;
            }
        }
        HashSet<String> intersect = new HashSet<>(first.keySet());
        //пересечение двух сетов
        intersect.retainAll(second.keySet());
        //которые нужно добавить/удалить
        result += first.size() + second.size() - 2 * intersect.size();
        return result;
    }
}
