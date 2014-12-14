package ru.fizteh.fivt.students.kuzmichevdima.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kuzmichevdima.JUnit.Interpreter.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class ValueWrapper {
    public String value;
    public boolean isRemoved;
    public boolean isContains;

    public ValueWrapper(String value, boolean isRemoved, boolean isContains) {
        this.value = value;
        this.isRemoved = isRemoved;
        this.isContains = isContains;
    }
}

public class DbTable implements Table {
    String tableName;
    private Path tablePath;
    private Map<String, ValueWrapper>  diff; //0 = out; 1 = remove
    private Map<String, String> state;

    static final int DIR_COUNT = 16;
    static final int FILE_COUNT = 16;
    static final String CODING = "UTF-8";

    public DbTable(Path path, String name) throws RuntimeException {
        tableName = name;
        tablePath = path;
        diff = new HashMap<>();
        try {
            readFromDisk();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error reading table " + tableName + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return tableName;
    }

    public int getDiffCount() {
        return diff.size();
    }

    @Override
    public String get(String key) {
        Utils.checkOnNull(key, null);
        String value = null;
        if (diff.containsKey(key)) {
            ValueWrapper valueWrapper = diff.get(key);
            if (!valueWrapper.isRemoved) {
                value = valueWrapper.value;
            }
        } else {
            value = state.get(key);
        }
        return value;
    }

    @Override
    public String put(String key, String value) {
        Utils.checkOnNull(key, null);
        Utils.checkOnNull(value, null);
        String oldValue = null;
        boolean isContains = false;
        if (state.containsKey(key)) {
            isContains = true;
        }
        if (diff.containsKey(key)) {
            ValueWrapper valueWrapper = diff.get(key);
            if (valueWrapper.isRemoved) {
                valueWrapper.isRemoved = false;
            } else {
                oldValue = valueWrapper.value;
                diff.remove(key);
                diff.put(key, new ValueWrapper(value, false, isContains));
            }
        } else {
            if (state.containsKey(key)) {
                oldValue = state.get(key);
            }
            diff.put(key, new ValueWrapper(value, false, isContains));
        }
        return oldValue;
    }

    @Override
    public String remove(String key) {
        Utils.checkOnNull(key, null);
        String removedValue = null;
        if (diff.containsKey(key)) {
            ValueWrapper valueWrapper = diff.get(key);
            if (!valueWrapper.isRemoved) {
                removedValue = diff.get(key).value;
                diff.remove(key);
                if (valueWrapper.isContains) {
                    diff.put(key, new ValueWrapper(removedValue, true, true));
                }
            }
        } else {
            if (state.containsKey(key)) {
                removedValue = state.get(key);
                diff.put(key, new ValueWrapper(removedValue, true, true));
            }
        }
        return removedValue;
    }

    @Override
    public int size() {
        int recordsCount = state.size();
        for (Map.Entry<String, ValueWrapper> f : diff.entrySet()) {
            ValueWrapper valueWrapper = f.getValue();
            if (valueWrapper.isRemoved) {
                recordsCount--;
            } else {
                if (!valueWrapper.isContains) {
                    recordsCount++;
                }
            }
        }
        return recordsCount;
    }

    @Override
    public int commit() throws RuntimeException {
        int commitCount = diff.size();
        for (Map.Entry<String, ValueWrapper> f : diff.entrySet()) {
            ValueWrapper valueWrapper = f.getValue();
            if (valueWrapper.isRemoved) {
                state.remove(f.getKey());
            } else {
                if (valueWrapper.isContains) {
                    state.remove(f.getKey());
                }
                state.put(f.getKey(), valueWrapper.value);
            }
        }
        try {
            writeToDisk();
        } catch (IOException e) {
            throw new RuntimeException("Error writing table " + tableName, e);
        }
        diff.clear();
        return commitCount;
    }

    @Override
    public int rollback() {
        int diffCount = diff.size();
        diff.clear();
        return diffCount;
    }

    @Override
    public List<String> list() {
        List<String> list = new LinkedList<>();
        Set<String> keySet = new HashSet<>();
        for (Map.Entry<String, String> f : state.entrySet()) {
            keySet.add(f.getKey());
        }
        for (Map.Entry<String, ValueWrapper> f : diff.entrySet()) {
            ValueWrapper valueWrapper = f.getValue();
            if (valueWrapper.isRemoved) {
                keySet.remove(f.getKey());
            } else {
                if (!valueWrapper.isContains) {
                    keySet.add(f.getKey());
                }
            }
        }
        list.addAll(keySet);
        return list;
    }

    public void readFromDisk() throws IOException {

        state = new HashMap<>();
        try {
            for (int dir = 0; dir < DIR_COUNT; ++dir) {
                for (int file = 0; file < FILE_COUNT; ++file) {
                    Path filePath = tablePath.resolve(dir + ".dir").resolve(file + ".dat");
                    if (Files.exists(filePath)) {
                        DataInputStream in = new DataInputStream(Files.newInputStream(filePath));
                        while (true) {
                            try {
                                String key = readWord(in);
                                String value = readWord(in);
                                state.put(key, value);
                            } catch (EOFException e) {
                                break;
                            }
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new IOException("Key or value can't be encoded to " + CODING, e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Key is in a wrong file or directory", e);
        } catch (EOFException e) {
            throw new IOException("File breaks unexpectedly", e);
        } catch (IOException e) {
            throw new IOException("Unable read from file", e);
        }
        for (File directory : tablePath.toFile().listFiles()) {
            int k = directory.getName().indexOf('.');
            if ((k < 0) || !(directory.getName().endsWith(".dir"))) {
                throw new RuntimeException("Table subdirectories don't have appropriate name");
            }
            File[] directoryFiles = directory.listFiles();
            if (directory.list().length == 0) {
                throw new RuntimeException("Table has the wrong format");
            }
            for (File file : directoryFiles) {
                k = file.getName().indexOf('.');
                if ((k < 0) || !(file.getName().endsWith(".dat"))) {
                    throw new RuntimeException("Table subdirectory's files doesn't have appropriate name");
                }
            }
        }
    }

    private String readWord(DataInputStream in) throws IOException {
        int length = in.readInt();
        byte[] word = new byte[length];
        in.read(word, 0, length);
        String str = new String(word, CODING);
        return str;
    }

    public void writeToDisk() throws IOException {
        if (!Files.exists(tablePath)) {
            String tableToCreatePath = tablePath.toString();
            File tableDirectory = new File(tableToCreatePath);
            if (!tableDirectory.mkdir()) {
                throw new IOException("Can't create table directory");
            }
        }
        Map<String, String>[][] db = new Map[DIR_COUNT][FILE_COUNT];
        for (int i = 0; i < DIR_COUNT; i++) {
            for (int j = 0; j < FILE_COUNT; j++) {
                db[i][j] = new HashMap<>();
            }
        }
        for (Map.Entry<String, String> entry : state.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Integer hashCode = Math.abs(key.hashCode());
            Integer dir = hashCode % DIR_COUNT;
            Integer file = hashCode / DIR_COUNT % FILE_COUNT;
            db[dir][file].put(key, value);
        }
        for (int i = 0; i < DIR_COUNT; i++) {
            for (int j = 0; j < FILE_COUNT; j++) {
                if (!db[i][j].isEmpty()) {
                    Integer nDir = i;
                    Integer nFile = j;
                    Path dirPath = tablePath.resolve(nDir + ".dir");
                    String newPath = dirPath.toString();
                    File directory = new File(newPath);
                    if (!directory.exists()) {
                        if (!directory.mkdir()) {
                            throw new IOException("Cannot create directory");
                        }
                    }
                    String newFilePath = dirPath.resolve(nFile + ".dat").toString();
                    File file = new File(newFilePath);
                    if (!file.exists()) {
                        if (!file.createNewFile()) {
                            throw new IOException("Cannot create file");
                        }
                    }
                    DataOutputStream out = new DataOutputStream(
                            new FileOutputStream(newFilePath));
                    for (Map.Entry<String, String> entry : db[i][j].entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        writeWord(out, key);
                        writeWord(out, value);
                    }
                    out.close();
                } else {
                    Integer nDir = i;
                    Integer nFile = j;
                    Path dirPath = tablePath.resolve(nDir + ".dir");
                    String newPath = dirPath.toString();
                    File directory = new File(newPath);
                    if (directory.exists()) {
                        String newFilePath = dirPath.resolve(nFile + ".dat").toString();
                        File file = new File(newFilePath);
                        Files.deleteIfExists(file.toPath());
                    }
                }
            }
        }
        File pathDirectory =  tablePath.toFile();
        File[] tableDirectories = pathDirectory.listFiles();
        for (File directory: tableDirectories) {
            File[] directoryFiles = directory.listFiles();
            if (directoryFiles.length == 0) {
                Files.delete(directory.toPath());
            }
        }
    }

    private void writeWord(DataOutputStream out, String word) throws IOException {
        byte[] byteWord = word.getBytes(CODING);
        out.writeInt(byteWord.length);
        out.write(byteWord);
        out.flush();
    }
}
