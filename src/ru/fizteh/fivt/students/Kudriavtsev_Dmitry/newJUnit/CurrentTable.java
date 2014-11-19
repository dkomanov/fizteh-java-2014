package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.newJUnit;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by Дмитрий on 31.10.2014.
 */

public class CurrentTable extends HashMap<String, String> implements Table {
    protected static final int FILES_COUNT = 16;
    protected static final int DIRECTORIES_COUNT = 16;
    private final String encoding = "UTF-8";

    protected Path dbPath;

    protected Map<String, Integer> changedFiles = new TreeMap<>();
    protected Map<String, String> activeTable = new HashMap<>();
    protected Map<String, String> removed = new HashMap<>();
    protected Map<String, String> newKey = new HashMap<>();

    public CurrentTable(String name) {
        dbPath = new File(name).toPath();
    }

    public CurrentTable(Path path) throws IOException{
        dbPath = path.normalize();
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

    public CurrentTable() {
        dbPath = new File(System.getProperty("fizteh.db.dir")).toPath();
    }

    private Path nameOfPath(String nameOfTable, int i, int j) {
        if (nameOfTable.equals("")) {
            return dbPath.resolve(i + ".dir" + File.separator + j + ".dat");
        }
        return dbPath.resolve(nameOfTable + File.separator + i + ".dir" + File.separator + j + ".dat");
    }

    private Path nameOfPath(String nameOfTable, int i) {
        if (nameOfTable.equals("")) {
            return dbPath.resolve(i + ".dir" + File.separator);
        }
        return dbPath.resolve(nameOfTable + File.separator + i + ".dir" + File.separator);
    }

    public SimpleEntry<String, SimpleEntry<Integer, Integer>> whereToSave(String nameOfTable, String value) {
        int hashCode = value.hashCode();
        int d = hashCode % DIRECTORIES_COUNT;
        int f = hashCode / DIRECTORIES_COUNT % FILES_COUNT;
        return new SimpleEntry<>(
                nameOfPath(nameOfTable, d, f).toString(),
                new SimpleEntry<>(d, f));
    }

    public int countOfCollisionsInFile(Path path) {
        int count = 0;
        try (DataInputStream iStream = new DataInputStream(Files.newInputStream(path))) {
            while (iStream.available() > 0) {
                int keySize = iStream.readInt();
                if (keySize > Integer.bitCount(Integer.MAX_VALUE)) {
                    System.err.println("Bad data in " + path.toString());
                    iStream.close();
                    return -1;
                }
                byte[] key = new byte[keySize];
                if (iStream.read(key, 0, keySize) == -1) {
                    endOfStream();
                }

                int valueSize = iStream.readInt();
                byte[] value = new byte[valueSize];
                if (iStream.read(value, 0, valueSize) == -1) {
                    endOfStream();
                }
                ++count;
            }
        } catch (IOException e) {
            System.err.println("It was exception on creating stream or in reading from file " + path.toString());
            return -1;
        }
        return count;
    }

    private void endOfStream() {
        System.err.println("there is no more data because the end of the stream has been reached.");
    }

     private void readFromFile(String nameOfTable, DataInputStream iStream, int i, int j) {
        try {
            int keySize = iStream.readInt();
            if (keySize > Integer.bitCount(Integer.MAX_VALUE)) {
                System.err.println("Bad data in " + nameOfPath(nameOfTable, i, j).toString());
                iStream.close();
                return;
            }
            byte[] key = new byte[keySize];
            if (iStream.read(key, 0, keySize) == -1) {
                endOfStream();
            }

            int valueSize = iStream.readInt();
            byte[] value = new byte[valueSize];
            if (iStream.read(value, 0, valueSize) == -1) {
                endOfStream();
            }

            activeTable.put(new String(key, encoding), new String(value, encoding));
        } catch (IOException e) {
            System.err.println("Error in reading: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void load(String nameOfTable) {
        for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
            for (int j = 0; j < FILES_COUNT; ++j) {
                try (DataInputStream stream = new DataInputStream(Files.newInputStream(
                        nameOfPath("", i, j)))) {
                    while (stream.available() > 0) {
                        readFromFile(nameOfTable, stream, i, j);
                    }
                } catch (IOException ignore) {
                    continue;
                }
            }
        }
        newKey.clear();
    }

    private void writeToFile(DataOutputStream oStream, String key, String value) throws IOException {
        byte[] keyInBytes = key.getBytes(encoding);
        byte[] valueInBytes = value.getBytes(encoding);

        oStream.writeInt(keyInBytes.length);
        oStream.write(keyInBytes);

        oStream.writeInt(valueInBytes.length);
        oStream.write(valueInBytes);
    }

    public void deleteFiles(String nameOfTable, boolean all) {
        try {
            for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
                for (int j = 0; j < FILES_COUNT; ++j) {
                    if (changedFiles.containsKey(nameOfPath(nameOfTable, i, j).toString()) || all) {
                        if (Files.exists(nameOfPath(nameOfTable, i, j))) {
                            Files.delete(nameOfPath(nameOfTable, i, j));
                        }
                        if (Files.exists(nameOfPath(nameOfTable, i)) && nameOfPath(nameOfTable, i).toFile().list().length == 0) {
                            Files.delete(nameOfPath(nameOfTable, i));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Can't delete from disk: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void unload(CurrentTable currentTable, String nameOfTable) {
        deleteFiles(nameOfTable, false);
        boolean[] dir = new boolean[DIRECTORIES_COUNT];
        DataOutputStream[][] streams = new DataOutputStream[DIRECTORIES_COUNT][FILES_COUNT];
        try {
            if (!Files.exists(dbPath)) {
                Files.createDirectory(dbPath);
            }
            for (Entry<String, String> entry :  currentTable.activeTable.entrySet()) {
                SimpleEntry<String, SimpleEntry<Integer, Integer>> pathOfFile = whereToSave("", entry.getKey());
                if (changedFiles.containsKey(pathOfFile.getKey())) {
                    int d = pathOfFile.getValue().getKey();
                    int f = pathOfFile.getValue().getValue();
                    if (streams[d][f] == null) {
                        if (!dir[d]) {
                            if (!Files.exists(nameOfPath("", d))) {
                                Files.createDirectory(nameOfPath("", d));
                            }
                            dir[d] = true;
                        }
                        streams[d][f] = new DataOutputStream(Files.newOutputStream(
                                nameOfPath("", d, f)));
                    }
                    writeToFile(streams[d][f], entry.getKey(), entry.getValue());
                    Integer collisionCount = changedFiles.get(pathOfFile.getKey());
                    if (collisionCount > 0) {
                        --collisionCount;
                        changedFiles.put(pathOfFile.getKey(), collisionCount);
                    } else {
                        changedFiles.remove(pathOfFile.getKey());
                    }
                }
            }
            changedFiles.clear();
        } catch (IOException e) {
            System.err.println("Can't write to disk: " + e.getMessage());
            changedFiles.clear();
            System.exit(-1);
        } finally {
            for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
                for (int j = 0; j < FILES_COUNT; ++j) {
                    if (streams[i][j] != null) {
                        try {
                            streams[i][j].close();
                        } catch (IOException ignored) {
                            continue;
                        }
                    }
                }
            }
        }
    }

    private int remindChanges(boolean flag) {
        int removedCount = 0;
        if (!flag) {
            for (String s : removed.keySet()) {
                if (activeTable.containsKey(s)) {
                    ++removedCount;
                }
            }
        } else {
            removedCount = removed.size();
        }
        int count = removedCount + newKey.size();
        removed.clear();
        newKey.clear();
        return count;
    }

    @Override
    public String getName() {
        return dbPath.getFileName().toString();
    }

    @Override
    public int size() {
        return list().size();
    }

    @Override
    public java.util.List<String> list() {
        java.util.List<String> result = newKey.keySet().stream().collect(Collectors.toList());
        result.addAll(activeTable.keySet().stream().filter(key -> !newKey.containsKey(key)).collect(Collectors.toList()));
        return result;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (newKey.containsKey(key)) {
            return newKey.get(key);
        }
        if (!activeTable.containsKey(key) || removed.containsKey(key)) {
            return null;
        }
        return activeTable.get(key);
    }

    @Override
    public int commit() {
        removed.keySet().forEach(activeTable::remove);
        for (Entry<String, String> entry : newKey.entrySet()) {
            activeTable.put(entry.getKey(), entry.getValue());
        }
        unload(this, getName());
        return remindChanges(true);
    }

    @Override
    public int rollback() {
        return remindChanges(false);
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (!activeTable.containsKey(key) && !newKey.containsKey(key)) {
            return null;
        }
        if (!newKey.containsKey(key)) {
            removed.put(key, activeTable.get(key));
            return activeTable.get(key);
        } else {
            String value = newKey.get(key);
            //removed.put(key, value);
            newKey.remove(key);
            return value;
        }
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        if (activeTable.containsKey(key) && activeTable.get(key).equals(value)) {
            if (newKey.containsKey(key)) {
                newKey.remove(key);
            }
            return value;
        }
        removed.remove(key);
        return newKey.put(key, value);
    }

}
