package ru.fizteh.fivt.students.alexpodkin.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 23.11.14.
 */
public class StoreableTable implements Table {

    private final int mod = 16;
    private String dirPath;
    private String tableName;
    List<Class<?>> signature;
    private Map<String, Storeable> table;
    private int uncommitedChanges;
    private Reader reader;
    private Writer writer;
    private StoreableTableProvider storeableTableProvider;

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
        //removeData(new File(dirPath + File.separator + tableName));
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
                File subDir = new File(path);
                for (int j = 0; j < mod; j++) {
                    String subPath = path + File.separator + Integer.toString(j) + ".dat";
                    File file = new File(subPath);
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

    public StoreableTable(StoreableTableProvider stp, String name, String dir, List<Class<?>> sign) {
        storeableTableProvider = stp;
        tableName = name;
        dirPath = dir;
        signature = sign;
        table = new HashMap<>();
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
        uncommitedChanges++;
        return table.put(key, value);
    }

    @Override
    public Storeable remove(String key) {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        if (key == null) {
            throw new IllegalArgumentException("Arguments shouldn't be null");
        }
        if (table.containsKey(key)) {
            uncommitedChanges++;
        }
        return table.remove(key);
    }

    @Override
    public int size() {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        return table.size();
    }

    @Override
    public int commit() throws IOException {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        int ans = uncommitedChanges;
        if (uncommitedChanges > 0) {
            uncommitedChanges = 0;
            writeData();
        }
        return ans;
    }

    @Override
    public int rollback() {
        if (storeableTableProvider.getTable(tableName) == null) {
            throw new IllegalArgumentException("Table doesn't exist");
        }
        int ans = uncommitedChanges;
        uncommitedChanges = 0;
        readData();
        return ans;
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
        return table.get(key);
    }
}
