package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import ru.fizteh.fivt.students.LebedevAleksey.FileMap.LoadOrSaveError;

import java.nio.file.Path;

public class Table extends ru.fizteh.fivt.students.LebedevAleksey.FileMap.Table {
    public static final int FILES_COUNT = 16;
    protected static final int SUBDIRECTORIES_COUNT = 16;
    private TablePart[][] structuredParts;
    private String tableName;
    private Database database;

    public Table(String name, Database databaseParent) {
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

    public String getTableName() {
        return tableName;
    }

    @Override
    public ru.fizteh.fivt.students.LebedevAleksey.FileMap.TablePart getPartForKey(String key) throws LoadOrSaveError {
        return super.getPartForKey(key);
    }

    public Path getDirectory() throws DatabaseFileStructureException {
        return database.getRootDirectoryPath().resolve(tableName);
    }
}
