package ru.fizteh.fivt.students.alexpodkin.Parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.alexpodkin.Storeable.Reader;
import ru.fizteh.fivt.students.alexpodkin.Storeable.Writer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Alex on 25.11.14.
 */
public class ParallelTable implements Table {

    private final int mod = 16;
    private String dirPath;
    private String tableName;
    List<Class<?>> signature;
    private Map<String, Storeable> table;
    private ThreadLocal<Integer> uncommitedChanges;
    private ThreadLocal<Integer> num;
    private Reader reader;
    private Writer writer;
    private ParallelTableProvider storeableTableProvider;
    private ReentrantReadWriteLock lock;
    private ThreadLocal<Map<String, Storeable>> addedChanges;
    private ThreadLocal<Set<String>> removedElements;

    private void checkSignature(Storeable storeable) {
        try {
            for (int i = 0; i < signature.size(); i++) {
                if (storeable.getColumnAt(i) != null) {
                    if (!storeable.getColumnAt(i).getClass().equals(signature.get(i))) {
                        throw new ColumnFormatException("Bad column format");
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("Bad column format");
        }
    }

    private boolean checkSubDirectory(File file) {
        for (File subFile : file.listFiles()) {
            if (subFile.isDirectory() || !checkName(subFile.getName(), ".dat")) {
                return false;
            }
        }
        return true;
    }

    private boolean checkName(String name, String s) {
        try {
            if (name.length() < 4) {
                return false;
            }
            if (!name.endsWith(s)) {
                return false;
            }
            int num = Integer.parseInt(name.replace(s, ""));
            if (num < 0 || num > 16) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void readData() {
        table = new HashMap<>();
        File tableDir = new File(dirPath + File.separator + tableName);
        for (File file : tableDir.listFiles()) {
            if ("signature.tsv".equals(file.getName())) {
                continue;
            }
            if (!file.isDirectory() || !checkName(file.getName(), ".dir") || !checkSubDirectory(file)) {
                throw new RuntimeException("Wrong data");
            }
            for (File subFile : file.listFiles()) {
                reader = new Reader(subFile.getAbsolutePath(),
                        storeableTableProvider, storeableTableProvider.getTable(tableName));
                try {
                    HashMap<String, Storeable> subTable = reader.readDataFromFile();
                    for (HashMap.Entry<String, Storeable> entry : subTable.entrySet()) {
                        table.put(entry.getKey(), entry.getValue());
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Bad reading");
                }
            }
        }
    }

    private void writeData() {
        try {
            HashMap<String, Storeable>[][] maps = new HashMap[mod][mod];
            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < mod; j++) {
                    maps[i][j] = new HashMap<>();
                }
            }
            for (String key : table.keySet()) {
                int hash = key.hashCode();
                int dirNum = (hash % mod + mod) % mod;
                int fNum = (hash / mod % mod + mod) % mod;
                maps[dirNum][fNum].put(key, table.get(key));
            }
            for (int i = 0; i < mod; i++) {
                String tablePath = dirPath + File.separator + tableName;
                String path = tablePath + File.separator + Integer.toString(i) + ".dir";
                for (int j = 0; j < mod; j++) {
                    String subPath = path + File.separator + Integer.toString(j) + ".dat";
                    writer = new Writer(subPath, storeableTableProvider, storeableTableProvider.getTable(tableName));
                    writer.writeDataToFile(maps[i][j], path);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Errors in writing");
        }
    }

    public boolean removeData(File toRemove) {
        if (toRemove.isDirectory()) {
            File[] files = toRemove.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (!removeData(file)) {
                        return false;
                    }
                }
            }
        }
        try {
            if (!toRemove.delete()) {
                throw new RuntimeException(toRemove.getAbsolutePath() + ": couldn't remove file");
            }
            return true;
        } catch (SecurityException e) {
            throw new RuntimeException(toRemove.getAbsolutePath() + ": couldn't remove file");
        }
    }

    public ParallelTable(ParallelTableProvider stp, String name, String dir, List<Class<?>> sign) {
        storeableTableProvider = stp;
        tableName = name;
        dirPath = dir;
        signature = sign;
        table = new HashMap<>();
        addedChanges = new ThreadLocal<Map<String, Storeable>>() {
            @Override
            protected Map<String, Storeable> initialValue() {
                return new HashMap<>();
            }
        };
        removedElements = new ThreadLocal<Set<String>>() {
            @Override
            protected Set<String> initialValue() {
                return new HashSet<>();
            }
        };
        uncommitedChanges = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };
        num = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };
        lock = new ReentrantReadWriteLock();
        readData();
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        if (key == null || value == null) {
            throw new IllegalArgumentException("Arguments shouldn't be null");
        }
        checkSignature(value);
        Storeable result;
        if (removedElements.get().contains(key)) {
            result = null;
            num.set(num.get() + 1);
        } else {
            if (addedChanges.get().containsKey(key)) {
                result = addedChanges.get().get(key);
            } else {
                lock.readLock().lock();
                try {
                    if (table.containsKey(key)) {
                        result = table.get(key);
                    } else {
                        result = null;
                        num.set(num.get() + 1);
                    }
                } finally {
                    lock.readLock().unlock();
                }
            }
        }
        removedElements.get().remove(key);
        addedChanges.get().put(key, value);
        uncommitedChanges.set(uncommitedChanges.get() + 1);
        return result;
    }

    @Override
    public Storeable remove(String key) {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        if (key == null) {
            throw new IllegalArgumentException("Arguments shouldn't be null");
        }
        Storeable result;
        if (removedElements.get().contains(key)) {
            result = null;
        } else {
            if (addedChanges.get().containsKey(key)) {
                result = addedChanges.get().get(key);
                uncommitedChanges.set(uncommitedChanges.get() + 1);
                num.set(num.get() - 1);
            } else {
                lock.readLock().lock();
                try {
                    if (table.containsKey(key)) {
                        result = table.get(key);
                        uncommitedChanges.set(uncommitedChanges.get() + 1);
                        num.set(num.get() - 1);
                    } else {
                        result = null;
                    }
                } finally {
                    lock.readLock().unlock();
                }
            }
        }
        if (result != null) {
            addedChanges.get().remove(key);
            removedElements.get().add(key);
        }
        return result;
    }

    @Override
    public int size() {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        lock.readLock().lock();
        try {
            return table.size() + num.get();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<String> list() {
        lock.readLock().lock();
        Set<String> result = new HashSet<>();
        try {
            result = new HashSet<>(table.keySet());
        } finally {
            lock.readLock().unlock();
        }
        for (String key : addedChanges.get().keySet()) {
            result.add(key);
        }
        for (String key : removedElements.get()) {
            result.remove(key);
        }
        return new ArrayList<>(result);
    }

    @Override
    public int commit() throws IOException {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        int ans = uncommitedChanges.get();
        if (ans > 0) {
            uncommitedChanges.set(0);
            lock.writeLock().lock();
            try {
                for (Map.Entry<String, Storeable> entry : addedChanges.get().entrySet()) {
                    table.put(entry.getKey(), entry.getValue());
                }
                for (String key : removedElements.get()) {
                    table.remove(key);
                }
                writeData();
            } finally {
                lock.writeLock().unlock();
            }
        }
        return ans;
    }

    @Override
    public int rollback() {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        int ans = uncommitedChanges.get();
        uncommitedChanges.set(0);
        num.set(0);
        addedChanges.get().clear();
        removedElements.get().clear();
        return ans;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return uncommitedChanges.get();
    }

    @Override
    public int getColumnsCount() {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        return signature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        if (columnIndex >= getColumnsCount()) {
            throw new IndexOutOfBoundsException("Invalid column index");
        }
        return signature.get(columnIndex);
    }

    @Override
    public String getName() {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        return tableName;
    }

    @Override
    public Storeable get(String key) {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        if (key == null) {
            throw new IllegalArgumentException("Arguments shouldn't be null");
        }
        Storeable result;
        if (removedElements.get().contains(key)) {
            result = null;
        } else {
            if (addedChanges.get().containsKey(key)) {
                result = addedChanges.get().get(key);
            } else {
                lock.readLock().lock();
                try {
                    result = table.get(key);
                } finally {
                    lock.readLock().unlock();
                }
            }
        }
        return result;
    }
}
