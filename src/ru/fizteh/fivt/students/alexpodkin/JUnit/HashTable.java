package ru.fizteh.fivt.students.alexpodkin.JUnit;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 10.11.14.
 */
public class HashTable implements Table {

    private final int mod = 16;
    private String tableName;
    private Map<String, String> table;
    private int uncommittedChanges;
    private String dirPath;
    private Reader reader;
    private Writer writer;

    private boolean recursiveRemove(File toRemove) {
        if (toRemove.isDirectory()) {
            File[] files = toRemove.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (!recursiveRemove(file)) {
                        return false;
                    }
                }
            }
        }
        try {
            if (!toRemove.delete()) {
                return false;
            }
            return true;
        } catch (SecurityException e) {
            System.err.println("Security exeption");
        }
        return false;
    }

    public boolean removeData() {
        return recursiveRemove(new File(dirPath));
    }

    private void readData() {
        try {
            HashMap<String, String> subTable = new HashMap<>();
            File db = new File(dirPath);
            File[] subdir = db.listFiles();
            for (File dir : subdir) {
                if (!dir.isDirectory()) {
                    throw new RuntimeException("Bad input db");
                }
                File[] tables = dir.listFiles();
                for (File sub : tables) {
                    reader = new Reader(sub.getAbsolutePath());
                    subTable = reader.readDataFromFile();
                }
                for (Map.Entry<String, String> entry : subTable.entrySet()) {
                    table.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Something goes wrong with reading");
        }
    }

    private void writeData() {
        try {
            HashMap<String, String>[][] db = new HashMap[mod][mod];
            for (int i = 0; i < mod; i++) {
                for (int j = 0; j < mod; j++) {
                    db[i][j] = new HashMap<>();
                }
            }
            for (Map.Entry<String, String> entry : table.entrySet()) {
                int hash = entry.getKey().hashCode();
                int dirNum = hash % mod;
                int fileNum = hash / mod % mod;
                db[dirNum][fileNum].put(entry.getKey(), entry.getValue());
            }
            for (int i = 0; i < mod; i++) {
                String subDirPath = dirPath + File.separator + Integer.toString(i) + ".dir";
                for (int j = 0; j < mod; j++) {
                    String filePath = subDirPath + File.separator + Integer.toString(j) + ".dat";
                    writer = new Writer(filePath);
                    writer.writeDataToFile(db[i][j], subDirPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Something goes wrong with writing");
        }
    }

    public HashTable(String name, String path) {
        tableName = name;
        dirPath = path + File.separator + name;
        table = new HashMap<>();
        readData();
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key shouldn't be null");
        }
        return table.get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key shouldn't be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value shouldn't be null");
        }
        uncommittedChanges++;
        return table.put(key, value);
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key shouldn't be null");
        }
        if (table.containsKey(key)) {
            uncommittedChanges++;
        }
        return table.remove(key);
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public int commit() {
        int ans = uncommittedChanges;
        uncommittedChanges = 0;
        writeData();
        return ans;
    }

    @Override
    public int rollback() {
        int ans = uncommittedChanges;
        uncommittedChanges = 0;
        readData();
        return ans;
    }

    @Override
    public List<String> list() {
        return new ArrayList<>(table.keySet());
    }
}
