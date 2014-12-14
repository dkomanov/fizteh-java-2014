package ru.fizteh.fivt.students.lukina.DataBase;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DBase implements Table, AutoCloseable {
    private static String rootDir = "";
    protected ThreadLocal<HashMap<String, Storeable>> dataMap = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        public HashMap<String, Storeable> initialValue() {
            return new HashMap<>();
        }
    };
    protected HashMap<String, Storeable> commonDataMap = new HashMap<>();
    protected ArrayList<Class<?>> typesList;
    protected DBaseProvider provider;
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(
            true);
    // private Lock read = readWriteLock.readLock();
    // private Lock write = readWriteLock.writeLock();
    private String currTable = "";
    private volatile boolean closed = false;

    public DBase(String tableName, String root, TableProvider prov) {
        if (prov == null) {
            throw new IllegalArgumentException("Provider is null");
        }
        provider = (DBaseProvider) prov;
        if (tableName == null) {
            throw new IllegalArgumentException("table name is null");
        } else {
            currTable = tableName;
        }
        if (root == null || root.isEmpty()) {
            throw new IllegalArgumentException("Root name is empty");
        }
        if (!(new File(root).exists())) {
            throw new IllegalStateException("Root not exists");
        }
        if (root.endsWith(File.separator)) {
            rootDir = root;
        } else {
            rootDir = root + File.separatorChar;
        }
        if (!(new File(rootDir + tableName).exists())) {
            throw new IllegalStateException("Table not exists");
        }
    }

    public void setRemoved() {
        closed = true;
    }

    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("table is closed");
        }
    }

    public void setHashMap(HashMap<String, Storeable> map) {
        copyMap(commonDataMap, map);
    }

    public void setTypes(List<Class<?>> columnTypes) {
        typesList = (ArrayList<Class<?>>) columnTypes;
    }

    protected File getDirWithNum(int dirNum) {
        File res = new File(rootDir + currTable + File.separatorChar + dirNum
                + ".dir");
        return res;
    }

    protected File getFileWithNum(int fileNum, int dirNum) {
        File res = new File(rootDir + currTable + File.separatorChar + dirNum
                + ".dir" + File.separatorChar + fileNum + ".dat");
        return res;
    }

    protected void unloadData() {
        RandomAccessFile[] filesArray = new RandomAccessFile[256];
        try {
            for (int i = 0; i < 16; ++i) {
                DBaseProvider.doDelete(getDirWithNum(i));
            }
            Set<Entry<String, Storeable>> entries = commonDataMap.entrySet();
            for (Map.Entry<String, Storeable> entry : entries) {
                String key = entry.getKey();
                Storeable value = entry.getValue();
                if (value != null) {
                    byte[] keyBytes = key.getBytes("UTF-8");
                    String str = provider.serialize(this, value);
                    byte[] valueBytes = str.getBytes("UTF-8");
                    byte b = 0;
                    b = (byte) Math.abs(keyBytes[0]);
                    int ndirectory = b % 16;
                    int nfile = b / 16 % 16;
                    if (filesArray[ndirectory * 16 + nfile] == null) {
                        File directory = getDirWithNum(ndirectory);
                        if (!directory.exists()) {
                            if (!directory.mkdirs()) {
                                throw new RuntimeException(
                                        "Cannot unload data correctly: cannot create directory "
                                                + directory.getAbsolutePath());
                            }
                        }
                        File file = getFileWithNum(nfile, ndirectory);
                        if (!file.exists()) {
                            if (!file.createNewFile()) {
                                throw new RuntimeException(
                                        "Cannot unload data correctly: cannot create file "
                                                + file.getAbsolutePath());
                            }
                        }
                        filesArray[ndirectory * 16 + nfile] = new RandomAccessFile(
                                file, "rw");
                    }
                    filesArray[ndirectory * 16 + nfile]
                            .writeInt(keyBytes.length);
                    filesArray[ndirectory * 16 + nfile]
                            .writeInt(valueBytes.length);
                    filesArray[ndirectory * 16 + nfile].write(keyBytes);
                    filesArray[ndirectory * 16 + nfile].write(valueBytes);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot unload file correctly", e);
        } finally {
            for (int i = 0; i < 256; ++i) {
                if (filesArray[i] != null) {
                    try {
                        filesArray[i].close();
                    } catch (Throwable e) {
                        // not OK
                    }
                }
            }
        }
    }

    private void mergeMaps() {
        for (Map.Entry<String, Storeable> entry : dataMap.get().entrySet()) {
            String key = entry.getKey();
            Storeable val = entry.getValue();
            if (val == null) {
                commonDataMap.remove(key);
            } else {
                commonDataMap.put(key, val);
            }
        }
    }

    private void copyMap(HashMap<String, Storeable> dest,
                         HashMap<String, Storeable> source) {
        dest.clear();
        Set<Map.Entry<String, Storeable>> entries = source.entrySet();
        for (Map.Entry<String, Storeable> entry : entries) {
            dest.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String getName() {
        checkClosed();
        return currTable;
    }

    private void checkKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (key.isEmpty() || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key is empty");
        }
        if (key.split("\\s").length > 1 || key.contains("\t")
                || key.contains(System.lineSeparator())) {
            throw new IllegalArgumentException("Key contains whitespaces");
        }
    }

    @Override
    public Storeable get(String key) {
        checkClosed();
        checkKey(key);
        Storeable val = null;
        if (dataMap.get().containsKey(key)) {
            val = dataMap.get().get(key);
        } else {
            val = commonDataMap.get(key);
        }
        return val;
    }

    @Override
    public Storeable put(String key, Storeable value)
            throws ColumnFormatException {
        checkClosed();
        checkKey(key);
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        provider.checkColumns(this, value);
        Storeable oldValue = null;
        if (dataMap.get().containsKey(key)) {
            oldValue = dataMap.get().get(key);
        } else {
            oldValue = commonDataMap.get(key);
        }
        dataMap.get().put(key, value);
        return oldValue;
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        checkClosed();
        checkKey(key);
        Storeable val = null;
        if (dataMap.get().containsKey(key)) {
            val = dataMap.get().get(key);
        } else {
            val = commonDataMap.get(key);
        }
        if (val != null) {
            dataMap.get().put(key, null);
        }
        return val;
    }

    @Override
    public int size() {
        checkClosed();
        int size = 0;
        size = commonDataMap.size();
        for (Map.Entry<String, Storeable> entry : dataMap.get().entrySet()) {
            String key = entry.getKey();
            Storeable value = entry.getValue();
            if (value == null && commonDataMap.containsKey(key)) {
                --size;
            }
            if (value != null && !commonDataMap.containsKey(key)) {
                ++size;
            }
        }
        return size;
    }

    @Override
    public int commit() throws IOException {
        checkClosed();
        int changesCount = getNumberOfUncommittedChanges();
        mergeMaps();
        dataMap.get().clear();
        unloadData();
        return changesCount;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        int count = 0;
        Set<Map.Entry<String, Storeable>> entries = dataMap.get().entrySet();
        for (Map.Entry<String, Storeable> entry : entries) {
            String key = entry.getKey();
            Storeable value = entry.getValue();
            Storeable oldValue = null;
            oldValue = commonDataMap.get(key);
            if ((((value == null) || (oldValue == null)) && (value != oldValue))
                    || ((value != null) && (oldValue != null) && !provider
                    .serialize(this, value).equals(
                            provider.serialize(this, oldValue)))) {
                count++;
            }
        }

        return count;
    }

    @Override
    public int rollback() {
        checkClosed();
        int changesCount = getNumberOfUncommittedChanges();
        dataMap.get().clear();
        return changesCount;
    }

    @Override
    public int getColumnsCount() {
        checkClosed();
        return typesList.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex)
            throws IndexOutOfBoundsException {
        checkClosed();
        if (columnIndex < 0 || columnIndex >= typesList.size()) {
            throw new IndexOutOfBoundsException("Incorrect index "
                    + columnIndex);
        }
        return typesList.get(columnIndex);
    }

    @Override
    public void close() throws Exception {
        if (!closed) {
            rollback();
            closed = true;
        }
        provider.closeTable(currTable);
    }

    @Override
    public List<String> list() {
        List<String> keys = new Vector<String>();
        Set<String> commonFileKeySet = commonDataMap.keySet();
        Set<String> fileKeySet = dataMap.get().keySet();
        Set<String> f = new HashSet<String>(fileKeySet);
        if (commonFileKeySet != null) {
            for (String key : commonFileKeySet) {
                f.add(key);
            }
        }
        if (fileKeySet != null) {
            for (String key : f) {
                if (commonDataMap.containsKey(key)
                        && !dataMap.get().containsKey(key)
                        || dataMap.get().get(key) != null) {
                    keys.add(key);
                }
            }
        }
        return keys;
    }

}
