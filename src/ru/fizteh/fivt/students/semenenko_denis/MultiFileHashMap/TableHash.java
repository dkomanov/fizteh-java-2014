package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableHash implements Table {

    protected static final int FILES_COUNT = 16;
    protected static final int SUBDIRECTORIES_COUNT = 16;
    private TableFileDAT[][] structuredParts;
    private Map<String, String> uncommited = new HashMap<>();
    private Map<String, String> removed = new HashMap<>();
    private String tableName;
    private Database database;

    public TableHash(String name, Database databaseParent) {
        structuredParts = new TableFileDAT[SUBDIRECTORIES_COUNT][];
        for (int i = 0; i < SUBDIRECTORIES_COUNT; ++i) {
            structuredParts[i] = new TableFileDAT[FILES_COUNT];
            for (int j = 0; j < FILES_COUNT; j++) {
                structuredParts[i][j] = new TableFileDAT(this, i, j);
            }
        }
        tableName = name;
        database = databaseParent;
    }

    public String getTableName() {
        return tableName;
    }

    protected TableFileDAT selectPartForKey(String key)
            throws LoadOrSaveException {
        int hashcode = key.hashCode();
        int ndirectory = hashcode % SUBDIRECTORIES_COUNT;
        int nfile = hashcode / SUBDIRECTORIES_COUNT % FILES_COUNT;
        if (ndirectory < 0) {
            ndirectory += SUBDIRECTORIES_COUNT;
        }
        if (nfile < 0) {
            nfile += FILES_COUNT;
        }
        return structuredParts[ndirectory][nfile];
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null.");
        }
        return getDATFileForKey(key).get(key);
    }

    @Override
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("Key is null.");
        }
        String returnedValue = getDATFileForKey(key).put(key, value);
        if  (returnedValue == null) {
            uncommited.put(key, value);
        }
        else {
            uncommited.put(key, value);
            removed.put(key, returnedValue);
        }
        return returnedValue;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null.");
        }
        String returnedValue = getDATFileForKey(key).remove(key);
        if (returnedValue != null) {
           removed.put(key, returnedValue);
        }
        return returnedValue;
    }

    @Override
    public int size() {
        int result = 0;
        for (TableFileDAT[] dir : structuredParts) {
            for (TableFileDAT part : dir) {
                part.load();
                result += part.count();
            }
        }
        return result;
    }

    @Override
    public int commit() {
        save();
        int result = uncommited.size() + removed.size();
        uncommited.clear();
        removed.clear();
        return result;
    }

    @Override
    public int rollback() {
        for (String key : uncommited.keySet()) {
            getDATFileForKey(key).remove(key);
        }
        for (String key : removed.keySet()) {
            getDATFileForKey(key).put(key, removed.get(key));
        }
        int result = removed.size() + uncommited.size();
        uncommited.clear();
        removed.clear();
        return result;
    }

    @Override
    public List<String> list() {
        List<String> result = new ArrayList<>();
        for (TableFileDAT[] dir : structuredParts) {
            for (TableFileDAT part : dir) {
                part.load();
                result.addAll(part.list());
            }
        }
        return result;
    }

    public void drop() {
        try {
            File directory = getDirectory().toFile();
            for (TableFileDAT[] dir : structuredParts) {
                for (TableFileDAT part : dir) {
                    part.drop();
                }
            }
            if (!directory.delete()) {
                throw new LoadOrSaveException("Directory can't deleted. Warning: data lost.");
            }
        } catch (SecurityException ex) {
            throw new LoadOrSaveException("Access denied in deleting table.", ex);
        } catch (UnsupportedOperationException ex) {
            throw new LoadOrSaveException("Error in deleting table.", ex);
        }
    }

    public TableFileDAT getDATFileForKey(String key) {
        TableFileDAT tablePart = selectPartForKey(key);
        tablePart.load();
        return tablePart;
    }

    public void save() {
        for (TableFileDAT[] dir : structuredParts) {
            for (TableFileDAT part : dir) {
                if (part.isLoaded()) {
                    part.save();
                }
            }
        }
    }

    public Path getDirectory() throws DatabaseFileStructureException {
        return database.getRootDirectoryPath().resolve(tableName);
    }

}

