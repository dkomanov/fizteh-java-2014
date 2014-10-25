package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import ru.fizteh.fivt.students.LebedevAleksey.FileMap.LoadOrSaveError;

import java.io.File;
import java.nio.file.Path;

public class Table extends ru.fizteh.fivt.students.LebedevAleksey.FileMap.Table {
    protected static final int FILES_COUNT = 16;
    protected static final int SUBDIRECTORIES_COUNT = 16;
    private TablePart[][] structuredParts;
    private String tableName;
    private Database database;

    public Table(String name, Database databaseParent) {
        super();
        parts.clear();
        structuredParts = new TablePart[SUBDIRECTORIES_COUNT][];
        for (int i = 0; i < SUBDIRECTORIES_COUNT; ++i) {
            structuredParts[i] = new TablePart[FILES_COUNT];
            for (int j = 0; j < FILES_COUNT; j++) {
                structuredParts[i][j] = new TablePart(this, i, j);
                parts.add(structuredParts[i][j]);
            }
        }
        tableName = name;
        database = databaseParent;
    }

    @Override
    protected ru.fizteh.fivt.students.LebedevAleksey.FileMap.TablePart selectPartForKey(String key)
            throws LoadOrSaveError {
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

    public int count() throws LoadOrSaveError {
        int result = 0;
        for (TablePart[] dir : structuredParts) {
            for (TablePart part : dir) {
                part.load();
                result += part.count();
            }
        }
        return result;
    }

    public void drop() throws LoadOrSaveError {
        try {
            File directory = getDirectory().toFile();
            for (TablePart[] dir : structuredParts) {
                for (TablePart part : dir) {
                    part.drop();
                }
            }
            directory.delete();
        } catch (SecurityException ex) {
            throw new LoadOrSaveError("Access denied in deleting table.", ex);
        } catch (UnsupportedOperationException ex) {
            throw new LoadOrSaveError("Error in deleting table.", ex);
        }
    }
}
