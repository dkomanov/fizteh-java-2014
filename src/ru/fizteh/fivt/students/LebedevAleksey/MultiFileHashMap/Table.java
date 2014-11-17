package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class Table {
    protected static final int FILES_COUNT = 16;
    protected static final int SUBDIRECTORIES_COUNT = 16;
    private TablePart[][] structuredParts;
    private String tableName;
    private Database database;

    public Table(String name, Database databaseParent) {
        initParts();
        tableName = name;
        database = databaseParent;
    }

    protected void initParts() {
        structuredParts = new TablePart[SUBDIRECTORIES_COUNT][];
        for (int i = 0; i < SUBDIRECTORIES_COUNT; ++i) {
            structuredParts[i] = new TablePart[FILES_COUNT];
            for (int j = 0; j < FILES_COUNT; j++) {
                structuredParts[i][j] = new TablePart(this, i, j);
            }
        }
    }

    protected TablePart selectPartForKey(String key)
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

    public String getTableName() {
        return tableName;
    }

    public Path getDirectory() throws DatabaseFileStructureException {
        return database.getRootDirectoryPath().resolve(tableName);
    }

    public int count() throws LoadOrSaveException, DatabaseFileStructureException {
        int result = 0;
        for (TablePart[] dir : structuredParts) {
            for (TablePart part : dir) {
                part.load();
                result += part.count();
            }
        }
        return result;
    }

    public void drop() throws LoadOrSaveException, DatabaseFileStructureException {
        try {
            File directory = getDirectory().toFile();
            for (TablePart[] dir : structuredParts) {
                for (TablePart part : dir) {
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


    public boolean remove(String key) throws LoadOrSaveException, DatabaseFileStructureException {
        return getPartForKey(key).remove(key);
    }

    public String get(String key) throws LoadOrSaveException, DatabaseFileStructureException {
        return getPartForKey(key).get(key);
    }

    public String put(String key, String value) throws LoadOrSaveException, DatabaseFileStructureException {
        return getPartForKey(key).put(key, value);
    }

    public ArrayList<String> list() throws LoadOrSaveException, DatabaseFileStructureException {
        ArrayList<String> result = new ArrayList<>();
        for (TablePart[] dir : structuredParts) {
            for (TablePart part : dir) {
                part.load();
                result.addAll(part.list());
            }
        }
        return result;
    }

    public TablePart getPartForKey(String key) throws LoadOrSaveException, DatabaseFileStructureException {
        TablePart tablePart = selectPartForKey(key);
        tablePart.load();
        return tablePart;
    }

    public void save() throws LoadOrSaveException, DatabaseFileStructureException {
        for (TablePart[] dir : structuredParts) {
            for (TablePart part : dir) {
                if (part.isLoaded()) {
                    part.save();
                }
            }
        }
    }
}
