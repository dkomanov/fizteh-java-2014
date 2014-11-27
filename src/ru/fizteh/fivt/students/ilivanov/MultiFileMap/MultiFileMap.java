package ru.fizteh.fivt.students.ilivanov.MultiFileMap;

import ru.fizteh.fivt.students.ilivanov.MultiFileMap.TableInterfaces.Table;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MultiFileMap implements Table {
    private File location;
    private FileUsing[][] map;
    private HashMap<String, String> oldValues;
    private HashSet<String> newKeys;
    private final int arraySize = 16;

    public MultiFileMap(final File location) {
        if (location == null) {
            throw new IllegalArgumentException("null location");
        }
        this.location = location;
        map = new FileUsing[arraySize][arraySize];
        newKeys = new HashSet<>();
        oldValues = new HashMap<>();
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                String relative = String.format("%d.dir/%d.dat", i, j);
                File path = new File(location, relative);
                map[i][j] = new FileUsing(path);
            }
        }
    }

    public String getName() {
        return location.getName();
    }

    public String get(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("get: null argument");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("get: empty key");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        return map[dir][file].get(key);
    }

    public String put(final String key, final String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put: null argument(-s)");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("put: empty key");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        String result = map[dir][file].put(key, value);
        if (result != null) {
            if (!newKeys.contains(key)) {
                String diffValue = oldValues.get(key);
                if (diffValue == null) {
                    if (!result.equals(value)) {
                        oldValues.put(key, result);
                    }
                } else {
                    if (diffValue.equals(value)) {
                        oldValues.remove(key);
                    }
                }
            }
        } else {
            String diffValue = oldValues.get(key);
            if (diffValue == null) {
                newKeys.add(key);
            } else {
                if (diffValue.equals(value)) {
                    oldValues.remove(key);
                }
            }
        }
        return result;
    }

    public String remove(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("remove: null key");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("remove: empty key");
        }
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        String result = map[dir][file].remove(key);
        if (result != null) {
            if (newKeys.contains(key)) {
                newKeys.remove(key);
            } else if (oldValues.get(key) == null) {
                oldValues.put(key, result);
            }
        }
        return result;
    }

    public int size() {
        int size = 0;
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                size += map[i][j].size();
            }
        }
        return size;
    }

    public int commit() {
        int changes =  uncommittedChanges();
        writeToDisk();
        return changes;
    }

    public void writeToDisk() {
        if (location.exists() && !location.isDirectory()) {
            throw new RuntimeException("specified location isn't a directory");
        }
        if (!location.exists()) {
            throw new RuntimeException("specified location doesn't exist");
        }
        try {
            for (int dir = 0; dir < arraySize; dir++) {
                boolean dirRequired = false;
                for (int file = 0; file < arraySize; file++) {
                    if (!map[dir][file].empty()) {
                        dirRequired = true;
                        break;
                    }
                }
                String relative = String.format("%d.dir", dir);
                File directory = new File(location, relative);
                if (directory.exists() && !directory.isDirectory()) {
                    throw new RuntimeException(String.format("commit: %s is not a directory\n", relative));
                }
                if (!directory.exists() && dirRequired) {
                    if (!directory.mkdir()) {
                        throw new RuntimeException(String.format("commit: can't create directory %s\n", relative));
                    }
                }
                if (directory.exists()) {
                    for (int file = 0; file < arraySize; file++) {
                        File db = map[dir][file].getFile();
                        if (map[dir][file].empty()) {
                            if (db.exists()) {
                                if (!db.delete()) {
                                    throw new RuntimeException(
                                            String.format("commit: can't delete file %s\n", db.getCanonicalPath()));
                                }
                            }
                        } else {
                            try {
                                map[dir][file].writeToDisk();
                            } catch (Exception e) {
                                throw new RuntimeException(
                                        String.format("commit: error in file %d.dir/%d.dat\n", dir, file), e);
                            }
                        }
                    }
                    File[] list = directory.listFiles();
                    if (directory.listFiles() != null && list != null && list.length == 0) {
                        if (!directory.delete()) {
                            throw new RuntimeException(
                                    String.format("commit: can't delete directory %s\n",
                                            directory.getCanonicalPath()));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        oldValues.clear();
        newKeys.clear();
    }

    public int rollback() {
        int changes =  uncommittedChanges();
        loadFromDisk();
        return changes;
    }

    public void loadFromDisk() {
        clear();
        if (!location.getParentFile().exists() || !location.getParentFile().isDirectory()) {
            throw new RuntimeException("unable to create a table in specified directory: directory doesn't exist");
        }
        if (!location.exists()) {
            return;
        }
        if (location.exists() && !location.isDirectory()) {
            throw new RuntimeException("specified location isn't a directory");
        }
        for (int dir = 0; dir < arraySize; dir++) {
            String relative = String.format("%d.dir", dir);
            File directory = new File(location, relative);
            if (directory.exists() && !directory.isDirectory()) {
                throw new RuntimeException(String.format("%s is not a directory\n", relative));
            }
            if (directory.exists()) {
                for (int file = 0; file < arraySize; file++) {
                    File db = map[dir][file].getFile();
                    if (db.exists()) {
                        try {
                            map[dir][file].loadFromDisk();
                        } catch (Exception e) {
                            throw new RuntimeException(String.format("error in file %d.dir/%d.dat\n", dir, file), e);
                        }
                    }
                }
            }
        }
        if (!validate()) {
            throw new RuntimeException("keys' distribution among files is incorrect");
        }
        oldValues.clear();
        newKeys.clear();
    }

    private void clear() {
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                map[i][j].clear();
            }
        }
        oldValues.clear();
        newKeys.clear();
    }

    private boolean validate() {
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                for (String key : map[i][j].getKeysList()) {
                    int hashCode = Math.abs(key.hashCode());
                    int dir = hashCode % 16;
                    int file = hashCode / 16 % 16;
                    if (dir != i || file != j) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public List<String> list() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                list.addAll(Arrays.asList(map[i][j].getKeysList()));
            }
        }
        return list;
    }

    public int uncommittedChanges() {
        return newKeys.size() + oldValues.size();
    }
}
