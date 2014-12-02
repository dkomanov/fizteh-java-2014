package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.*;

import java.io.File;
import java.io.PrintStream;
import java.util.*;

/**
 * @author AlexeyZhuravlev
 */
public class MyTable implements Table {

    MultiTable virginTable;
    String name;
    FancyTable dirtyTable;
    ArrayList<Command> changes;

    protected MyTable(MultiTable passedTable, String passedName) {
        virginTable = passedTable;
        name = passedName;
        changes = new ArrayList<>();
        dirtyTable = new FancyTable();
        dirtyTable.importMap(passedTable);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        return dirtyTable.databases[dir][file].data.get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        String result = get(key);
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        dirtyTable.databases[dir][file].data.put(key, value);
        changes.add(new MultiPutCommand(key, value));
        return result;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String result = get(key);
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        dirtyTable.databases[dir][file].data.remove(key);
        changes.add(new MultiRemoveCommand(key));
        return result;
    }

    @Override
    public int size() {
        return dirtyTable.recordsNumber();
    }

    @Override
    public int commit() {
        PrintStream out = System.out;
        System.setOut(new PrintStream(new DummyOutputStream()));
        try {
            int ans = diffTables(virginTable, dirtyTable);
            for (Command command: changes) {
                command.executeOnTable(virginTable);
            }
            changes.clear();
            return ans;
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            System.setOut(out);
        }
    }

    @Override
    public int rollback() {
        int ans = diffTables(virginTable, dirtyTable);
        dirtyTable.importMap(virginTable);
        changes.clear();
        return ans;
    }

    @Override
    public List<String> list() {
        List<String> result = new LinkedList<>();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                result.addAll(dirtyTable.databases[i][j].data.keySet());
            }
        }
        return result;
    }

    public int unsavedChanges() {
        return diffTables(virginTable, dirtyTable);
    }

    private static int diffTables(MultiTable first, MultiTable second) {
        int result = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (first.databases[i][j] == null && second.databases[i][j] != null) {
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
            if (second.containsKey(key) && !second.get(key).equals(value)) {
                result++;
            }
        }
        HashSet<String> intersect = new HashSet<>(first.keySet());
        intersect.retainAll(second.keySet());
        result += first.size() + second.size() - 2 * intersect.size();
        return result;
    }

    public File getDirectory() {
        return virginTable.mainDir;
    }
}
