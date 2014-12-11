package ru.fizteh.fivt.students.PotapovaSofia.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.*;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;

class ValueWrapper {
    public Storeable value;
    public boolean isRemoved;
    public boolean isContains;

    public ValueWrapper(Storeable value, boolean isRemoved, boolean isContains) {
        this.value = value;
        this.isRemoved = isRemoved;
        this.isContains = isContains;
    }
}

public class DbTable implements Table {
    private String tableName;
    private Path tablePath;
    private Map<String, ValueWrapper>  diff;
    private Map<String, Storeable> state;
    private List<Class<?>> signature;
    private TableProvider tableProvider;

    static final int DIR_COUNT = 16;
    static final int FILE_COUNT = 16;

    public DbTable(final Path tablePath, final String tableName, final TableProvider provider) {
        this.tablePath = tablePath;
        this.tableName = tableName;
        state = new HashMap<>();
        diff = new HashMap<>();
        signature = new ArrayList<>();
        tableProvider = provider;
        try {
            readFromDisk();
        } catch (IOException e) {
            throw new RuntimeException("Error reading table '" + tableName + "'" + e.getMessage());
        }
    }

    public DbTable(final Path tablePath, final String tableName,
                   final Map<String, Storeable> records, List<Class<?>> columnTypes, final TableProvider provider) {
        this.tablePath = tablePath;
        this.tableName = tableName;
        state = records;
        tableProvider = provider;
        diff = new HashMap<>();
        signature = columnTypes;
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public Storeable get(String key) {
        checkOnNull(key, null);
        Storeable value = null;
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
    public Storeable put(String key, Storeable value) {
        checkOnNull(key, null);
        checkOnNull(value, null);
        Storeable oldValue = null;
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
    public Storeable remove(String key) {
        checkOnNull(key, null);
        Storeable removedValue = null;
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
    public int getNumberOfUncommittedChanges() {
        return diff.size();
    }

    @Override
    public int getColumnsCount() {
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return signature.get(columnIndex);
    }

    @Override
    public List<String> list() {
        List<String> list = new LinkedList<>();
        Set<String> keySet = new HashSet<>();
        for (Map.Entry<String, Storeable> f : state.entrySet()) {
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

    private void readFromDisk() throws IOException {
        Path signaturePath = tablePath.resolve(StoreableMain.TABLE_SIGNATURE);
        //read from signature.tsv
        try (RandomAccessFile read = new RandomAccessFile(signaturePath.toString(), "r")) {
            if (read.length() > 0) {
                while (read.getFilePointer() < read.length()) {
                    String string = read.readLine();
                    String[] types = string.trim().split(" ");
                    int i = 0;
                    for (String s : types) {
                        if (StoreableMain.availableTypes.containsKey(s)) {
                            signature.add(i, StoreableMain.availableTypes.get(s));
                            i += 1;
                        } else {
                            throw new IllegalArgumentException("Table signature is invalid");
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("Table signature is invalid");
            }
        }
        //read from files
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
                                state.put(key, tableProvider.deserialize(this, value));
                            } catch (EOFException e) {
                                break;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new IOException("Key or value can't be encoded to " + StoreableMain.CODING, e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Key is in a wrong file or directory", e);
        } catch (EOFException e) {
            throw new IOException("File breaks unexpectedly", e);
        } catch (IOException e) {
            throw new IOException("Unable read from file", e);
        }
        //check on right directory content
        if (!Files.exists(signaturePath)) {
            throw new IllegalArgumentException("Table signature file does not exist");
        }
        boolean signatureExists = false;
        for (File directory : tablePath.toFile().listFiles()) {
            int k = directory.getName().indexOf('.');
            if ((k < 0) || !((directory.getName().endsWith(".dir")) || (directory.getName().endsWith(".tsv")))) {
                throw new RuntimeException("Table subdirectories don't have appropriate name");
            }
            if (directory.getName().endsWith(".dir")) {
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
            } else {
                if (signatureExists) {
                    throw new IllegalArgumentException(directory.getName() + " must not exist");
                } else {
                    signatureExists = true;
                }
            }
        }
    }

    private String readWord(DataInputStream in) throws IOException {
        int length = in.readInt();
        byte[] word = new byte[length];
        in.read(word, 0, length);
        String str = new String(word, StoreableMain.CODING);
        return str;
    }

    private void writeToDisk() throws IOException {
        if (!Files.exists(tablePath)) {
            String tableToCreatePath = tablePath.toString();
            File tableDirectory = new File(tableToCreatePath);
            if (!tableDirectory.mkdir()) {
                throw new IOException("Can't create table directory");
            }
        }
        Map<String, Storeable>[][] db = new Map[DIR_COUNT][FILE_COUNT];
        for (int i = 0; i < DIR_COUNT; i++) {
            for (int j = 0; j < FILE_COUNT; j++) {
                db[i][j] = new HashMap<>();
            }
        }
        for (Map.Entry<String, Storeable> entry : state.entrySet()) {
            String key = entry.getKey();
            Storeable value = entry.getValue();
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
                    DataOutputStream out = new DataOutputStream(new FileOutputStream(newFilePath));
                    for (Map.Entry<String, Storeable> entry : db[i][j].entrySet()) {
                        String key = entry.getKey();
                        String value = tableProvider.serialize(this, entry.getValue());
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
            if (directory.getName().endsWith(".dir")) {
                File[] directoryFiles = directory.listFiles();
                if (directoryFiles.length == 0) {
                    Files.delete(directory.toPath());
                }
            }
        }
    }

    private void writeWord(DataOutputStream out, String word) throws IOException {
        byte[] byteWord = word.getBytes(StoreableMain.CODING);
        out.writeInt(byteWord.length);
        out.write(byteWord);
        out.flush();
    }

    private static void checkOnNull(Object name, String msg) {
        if (name == null) {
            throw new IllegalArgumentException(msg);
        }
    }
}
