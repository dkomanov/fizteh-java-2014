package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.*;

import java.io.PrintStream;
import java.util.*;

public class MyTable implements Table {
    public MyTable(MultiTable oldTable, String oldName) {
        cleanTable = oldTable;
        name = oldName;
        commands = new ArrayList<>();
        dirtyTable = new DirtyTable();
        dirtyTable.importData(oldTable);
    }

    public MultiTable cleanTable;
    public DirtyTable dirtyTable;
    public ArrayList<Command> commands;
    public String name;

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
        return dirtyTable.dataBases[dir][file].getDataBase().get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        String oldValue = this.get(key);
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        dirtyTable.dataBases[dir][file].getDataBase().put(key, value);
        commands.add(new MultiPutCommand(key, value));
        return oldValue;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        String oldValue = this.get(key);
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        dirtyTable.dataBases[dir][file].getDataBase().remove(key);
        commands.add(new MultiRemoveCommand(key));
        return oldValue;
    }

    @Override
    public int size() {
        return dirtyTable.tableSize();
    }

    @Override
    public int commit() {
        PrintStream normal = System.out;
        System.setOut(new PrintStream(new NoWriteOutputStream()));
        int changes = this.unsavedChanges();
        try {
            for (Command command : commands) {
                command.runOnTable(cleanTable);
            }
        } catch (Exception ex) {
            throw new RuntimeException();
        } finally {
            System.setOut(normal);
        }
        commands.clear();
        return changes;
    }

    @Override
    public int rollback() {
        int changes = this.unsavedChanges();
        dirtyTable.importData(cleanTable);
        commands.clear();
        return changes;
    }

    @Override
    public List<String> list() {
        List<String> listKeys = new LinkedList<>();
        for (int dir = 0; dir < 16; ++dir) {
            for (int file = 0; file < 16; ++file) {
                listKeys.addAll(dirtyTable.dataBases[dir][file].getDataBase().keySet());
            }
        }
        return listKeys;
    }

    public int unsavedChanges() {
        return diffTables(cleanTable, dirtyTable);
    }

    private int diffTables(MultiTable first, MultiTable second) {
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

    private int diffHashMaps(HashMap<String, String> first, HashMap<String, String> second) {
        int diff = 0;
        for (Map.Entry<String, String> entry : first.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (second.containsKey(key) && !second.get(key).equals(value)) {
                ++diff;
            }
        }
        HashSet<String> intersection = new HashSet<>(first.keySet());
        intersection.retainAll(second.keySet());
        diff += first.size() + second.size() - 2 * intersection.size();
        return diff;
    }
}
