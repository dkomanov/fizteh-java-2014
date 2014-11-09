package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class Table implements TableInterface {
    private static final int DIRECTORIES_COUNT = 16;
    private static final int FILES_COUNT = 16;
    
    private Path path;
    private int size;
    private Map<String, String> disk;
    private Map<String, String> diff;

    public Table(Path tablePath) throws IllegalArgumentException {
        if (tablePath == null) {
            throw new IllegalArgumentException("The table directory wasn't specified");
        }
        disk = new HashMap<>();
        diff = new HashMap<>();
        path = tablePath;
        try {
            if (Files.notExists(path)) {
                if (!path.getParent().toFile().canWrite()) {
                    throw new Exception(path.toString() + ": don't have permission to create the directory");
                }
                Files.createDirectory(path);
            } else if (!path.toFile().canRead()) {
                throw new Exception(path.toString() + ": don't have permission to read the directory");
            } else if (!Files.isDirectory(path)) {
                throw new Exception(path.toString() + ": isn't a directory");
            } else {
                loadTable();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public String getName() {
        return path.toFile().getName();
    }

    public int unsavedSize() {
        return diff.size();
    }

    public int size() {
        return size;
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Invalid key/value");
        }
        String diffValue = diff.put(key, value);
        String result = diffValue != null ? diffValue : disk.get(key);
        if (result == null) {
            ++size;
        }
        return result;
    }

    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Invalid key");
        }
        String diffValue = diff.get(key);
        return diffValue != null ? diffValue : disk.get(key);
    }

    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Invalid key");
        }
        String result = null;
        String diskValue = disk.get(key);
        if (diskValue != null) {
            String diffValue = diff.put(key, null);
            result = diffValue != null ? diffValue : diskValue;
        } else {
            result = diff.remove(key);
        }
        if (result != null) {
            --size;
        }
        return result;
    }

    public List<String> list() {
        List<String> list = new LinkedList<String>();
        for (String key : disk.keySet()) {
            if (!diff.containsKey(key)) {
                list.add(key);
            }
        }
        for (String key : diff.keySet()) {
            if (diff.get(key) != null) {
                list.add(key);
            }
        }
        return list;
    }

    public int commit() {
        int amount = diff.size();
        for (String key : diff.keySet()) {
            String value = diff.get(key);
            if (value != null) {
                disk.put(key, value);
            } else {
                disk.remove(key);
            }
        }
        diff.clear();
        try {
            saveTable();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return amount;
    }

    public int rollback() {
        int amount = diff.size();
        diff.clear();
        size = disk.size();
        return amount;
    }

    private static String readItem(RandomAccessFile file) throws Exception {
        int wordSize = file.readInt();
        byte[] word = new byte[wordSize];
        file.read(word, 0, wordSize);
        return new String(word, "UTF-8");
    }

    private void loadFile(String filename) throws Exception {
        RandomAccessFile file = new RandomAccessFile(filename, "r");
        while (file.getFilePointer() < file.length()) {
            try {
                String key = readItem(file);
                String value = readItem(file);
                disk.put(key, value);
            } catch (Exception | OutOfMemoryError e) {
                throw new Exception(filename + ": invalid file format");
            }
        }
        file.close();
    }

    private void loadDirectory(Path dir) throws Exception {
        for (int i = 0; i < FILES_COUNT; ++i) {
            Path file = dir.resolve(Integer.toString(i) + ".dat");
            if (Files.exists(file)) {
                if (!file.toFile().canRead()) {
                    throw new Exception(file.toString() + ": don't have permission to read the file");
                } else if (!file.toFile().isFile()) {
                    throw new Exception(file.toString() + ": isn't a normal file");
                } else {
                    loadFile(file.toString());
                }
            }
        }            
    }

    private void loadTable() throws Exception {
        disk.clear();
        for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
            Path dir = path.resolve(Integer.toString(i) + ".dir");
            if (Files.exists(dir)) {
                if (!dir.toFile().canRead()) {
                    throw new Exception(dir.toString() + ": don't have permission to read the directory");
                } else if (!Files.isDirectory(dir)) {
                    throw new Exception(dir.toString() + ": isn't a directory");
                } else {
                    loadDirectory(dir);
                }
            }
        }
        size = disk.size();
    }

    private static void writeItem(RandomAccessFile file, String word) throws Exception {
        byte[] byteWord = word.getBytes("UTF-8");
        file.writeInt(byteWord.length);
        file.write(byteWord);
    }

    private boolean saveFile(String filename, int dirIndex, int fileIndex) throws Exception {
        boolean hasWritten = false;
        RandomAccessFile file = new RandomAccessFile(filename, "rw");
        for (String key : disk.keySet()) {
            int hashcode = key.hashCode();
            int dirNumber = hashcode % DIRECTORIES_COUNT;
            int fileNumber = hashcode / DIRECTORIES_COUNT % FILES_COUNT;
            if (dirIndex == dirNumber && fileIndex == fileNumber) {
                writeItem(file, key);
                writeItem(file, disk.get(key));
                hasWritten = true;
            }
        }
        file.close();
        return hasWritten;
    }

    private void saveDirectory(Path dir, int dirIndex) throws Exception {
        int usedFiles = FILES_COUNT;
        for (int i = 0; i < FILES_COUNT; ++i) {
            Path file = dir.resolve(Integer.toString(i) + ".dat");
            if (Files.notExists(file)) {
                if (!file.getParent().toFile().canWrite()) {
                    throw new Exception(file.toString() + ": don't have permission to create the file");
                }
                Files.createFile(file);
            } else if (!file.toFile().canWrite()) {
                throw new Exception(file.toString() + ": don't have permission to write the file");
            } else if (!file.toFile().isFile()) {
                throw new Exception(file.toString() + ": isn't a normal file");
            }
            if (!saveFile(file.toString(), dirIndex, i)) {
                Files.deleteIfExists(file);
                --usedFiles;
            }
        }
        if (usedFiles == 0) {
            Files.deleteIfExists(dir);
        }
    }

    private void saveTable() throws Exception {
        for (int i = 0; i < DIRECTORIES_COUNT; ++i) {
            Path dir = path.resolve(Integer.toString(i) + ".dir");
            if (Files.notExists(dir)) {
                if (!dir.getParent().toFile().canWrite()) {
                    throw new Exception(dir.toString() + ": don't have permission to create the directory");
                }
                Files.createDirectory(dir);
            } else if (!dir.toFile().canWrite()) {
                throw new Exception(dir.toString() + ": don't have permission to write the directory");
            } else if (!Files.isDirectory(dir)) {
                throw new Exception(dir.toString() + ": isn't a directory");
            }
            saveDirectory(dir, i);
        }
    }
}
