package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class Table implements TableInterface {
    private static final int DIRECTORIES_COUNT = 16;
    private static final int FILES_COUNT = 16;
    
    private Path path;
    private Map<String, String> table;
    private Map<String, String> changes;

    private static String readItem(RandomAccessFile file) throws IOException {
        int wordSize = file.readInt();
        byte[] word = new byte[wordSize];
        file.read(word, 0, wordSize);
        return new String(word, "UTF-8");
    }

    private static void writeItem(RandomAccessFile file, String word) throws IOException {
        byte[] byteWord = word.getBytes("UTF-8");
        file.writeInt(byteWord.length);
        file.write(byteWord);
    }

    private void load() throws IllegalArgumentException, IOException {
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
                        throw new IllegalArgumentException(tableFile.toString() + ": is a directory");
                    }
                }            
            } else if (tableDir.toFile().isFile()) {
                throw new IllegalArgumentException(tableDir.toString() + ": is a normal file");
            }
        }
    }

    private void save() {
        try {
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
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public Table(Path tablePath) throws IllegalArgumentException {
        try {
            table = new HashMap<String, String>();
            changes = new HashMap<String, String>();
            path = tablePath;
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            } else if (Files.isDirectory(path)) {
                load();
            } else {
                throw new IllegalArgumentException(path.toString() + ": is a normal file");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public String getName() {
        return path.toFile().getName();
    }

    public int unsavedSize() {
        return changes.size();
    }

    public int size() {
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
        return size;
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("invalid key/value");
        }
        String oldValue = changes.get(key);
        changes.put(key, value);
        return oldValue;
    }

    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("invalid key");
        }
        String value;
        if (changes.containsKey(key)) {
            value = changes.get(key);
        } else {
            value = table.get(key);
        }
        return value;
    }

    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("invalid key");
        }
        String value = null;
        if (changes.containsKey(key)) {
            value = changes.get(key);
        } else if (table.containsKey(key)) {
            value = table.get(key);
        }
        if (changes.get(key) != null || table.get(key) != null) {
            changes.put(key, null);
        }
        return value;
    }

    public List<String> list() {
        List<String> list = new LinkedList<String>();
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
        return list;
    }

    public int commit() {
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
        return amount;
    }

    public int rollback() {
        int amount = changes.size();
        changes.clear();
        return amount;
    }
}
