package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Table {
    private static final int DIRECTORIES_COUNT = 16;
    private static final int FILES_COUNT = 16;
    
    private Path path;
    private Map<String, String> table;
    private Map<String, String> changes;
    private String commandOutput;

    private static String readItem(RandomAccessFile file) throws Exception {
        int wordSize = file.readInt();
        byte[] word = new byte[wordSize];
        file.read(word, 0, wordSize);
        return new String(word);
    }

    private static void writeItem(RandomAccessFile file, String word) throws Exception {
        file.writeInt(word.getBytes("UTF-8").length);
        file.write(word.getBytes("UTF-8"));
    }

    private void load() throws Exception {
        table.clear();
        for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
            Path tableDir = path.resolve(Integer.toString(i) + ".dir");
            if (Files.isDirectory(tableDir)) {
                for (int j = 0; j < FILES_COUNT; ++j) {
                    Path tableFile = tableDir.resolve(Integer.toString(j) + ".dat");
                    if (tableFile.toFile().isFile()) {
                        RandomAccessFile file = new RandomAccessFile(tableFile.toString(), "r");
                        while (file.getFilePointer() < file.length()) {
                            String key = readItem(file);
                            String value = readItem(file);
                            table.put(key, value);
                        }
                        file.close();
                    } else if (Files.isDirectory(tableFile)) {
                        throw new Exception(tableFile.toString() + ": is a directory");
                    }
                }            
            } else if (tableDir.toFile().isFile()) {
                throw new Exception(tableDir.toString() + ": is a normal file");
            }
        }
    }

    private void save() throws Exception {
        for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
            Path tableDir = path.resolve(Integer.toString(i) + ".dir");
            if (!Files.isDirectory(tableDir)) {
                Files.createDirectory(tableDir);
            }
            for (int j = 0; j < FILES_COUNT; ++j) {
                Path tableFile = tableDir.resolve(Integer.toString(j) + ".dat");
                if (!Files.exists(tableFile)) {
                    Files.createFile(tableFile);
                }
                RandomAccessFile file = new RandomAccessFile(tableFile.toString(), "rw");
                boolean hasWritten = false;
                for (String key : table.keySet()) {
                    int hashcode = key.hashCode();
                    int first = hashcode % DIRECTORIES_COUNT;
                    int second = hashcode / DIRECTORIES_COUNT % FILES_COUNT;
                    if (i == first && j == second) {
                        writeItem(file, key);
                        writeItem(file, table.get(key));
                        hasWritten = true;
                    }
                }
                file.close();
                if (!hasWritten) {
                    Files.deleteIfExists(tableFile);
                }
            }
            if (tableDir.toFile().list().length == 0) {
                Files.deleteIfExists(tableDir);
            }
        }
    }

    public Table(Path tablePath) throws Exception {
        commandOutput = null;
        table = new HashMap<String, String>();
        changes = new HashMap<String, String>();
        path = tablePath;
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        } else if (Files.isDirectory(path)) {
            load();
        } else {
            throw new Exception(path.toString() + ": is a normal file");
        }
    }

    public void printCommandOutput() throws Exception {
        if (commandOutput != null) {
            System.out.println(commandOutput);
            commandOutput = null;
        }
    }

    public String getName() throws Exception {
        commandOutput = null;
        return path.toFile().getName();
    }

    public int unsavedSize() throws Exception {
        commandOutput = null;
        return changes.size();
    }

    public int size() throws Exception {
        int size = 0;
        for (String key : table.keySet()) {
            if (!changes.containsKey(key)) {
                ++size;
            }
        }
        for (String key : changes.keySet()) {
            if (changes.get(key) != null) {
                ++size;
            }
        }
        commandOutput = String.format("%d", size);
        return size;
    }

    public String put(String key, String value) throws Exception {
        String oldValue = changes.get(key);
        commandOutput = (oldValue != null) ? String.format("overwrite\n%s", oldValue) : ("new");
        changes.put(key, value);
        return value;
    }

    public String get(String key) throws Exception {
        String value;
        if (changes.containsKey(key)) {
            value = changes.get(key);
        } else {
            value = table.get(key);
        }
        commandOutput = (value != null) ? String.format("found\n%s", value) : ("not found");
        return value;
    }

    public String remove(String key) throws Exception {
        String value = null;
        if (changes.containsKey(key)) {
            value = changes.get(key);
        } else if (table.containsKey(key)) {
            value = table.get(key);
        }
        if (changes.get(key) != null || table.get(key) != null) {
            changes.put(key, null);
        }
        commandOutput = String.format((value != null) ? "removed" : "not found");
        return value;
    }

    public List<String> list() throws Exception {
        List<String> list = new ArrayList<String>();
        for (String key : table.keySet()) {
            if (!changes.containsKey(key)) {
                list.add(key);
            }
        }
        for (String key : changes.keySet()) {
            if (changes.get(key) != null) {
                list.add(key);
            }
        }
        commandOutput = String.join(", ", list);
        return list;
    }

    public int commit() throws Exception {
        int amount = changes.size();
        for (String key : changes.keySet()) {
            String value = changes.get(key);
            if (value != null) {
                table.put(key, value);
            } else {
                table.remove(key);
            }
        }
        changes.clear();
        save();
        commandOutput = String.format("%d", amount);
        return amount;
    }

    public int rollback() throws Exception {
        int amount = changes.size();
        changes.clear();
        commandOutput = String.format("%d", amount);
        return amount;
    }
}
