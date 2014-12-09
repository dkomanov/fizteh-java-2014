package ru.fizteh.fivt.students.dsalnikov.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.MultiTable;
import ru.fizteh.fivt.students.dsalnikov.utils.FileMapUtils;
import ru.fizteh.fivt.students.dsalnikov.utils.NoTableException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class Database implements MultiTable {
    public StorableTable currentlyUsedTable;
    public TableProvider currentlyUsedTableProvider;
    private File dbFile;

    public Database(File databaseFile,
                    TableProvider tableProvider) {
        currentlyUsedTableProvider = tableProvider;
        dbFile = databaseFile;
    }

    @Override
    public void create(List<String> args) {
        String tableName = args.get(1);
        if (currentlyUsedTableProvider.getTable(tableName) == null) {
            try {
                List<Class<?>> columnTypes = FileMapUtils.createListOfTypes(args);
                currentlyUsedTableProvider.createTable(tableName, columnTypes);
                //         System.out.println("created");
            } catch (IOException exc) {
                throw new IllegalArgumentException(exc.getMessage());
            }
        }
    }

    @Override
    public void drop(String name) {
        if (currentlyUsedTable != null) {
            if (currentlyUsedTable.getName().equals(name)) {
                currentlyUsedTable = null;
            }
        }
        try {
            currentlyUsedTableProvider.removeTable(name);
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
        }
    }

    @Override
    public void use(String name) {
        currentlyUsedTable = (StorableTable) currentlyUsedTableProvider.getTable(name);
    }

    @Override
    public List<String> showTables() {
        throw new IllegalStateException("this function is not supported in that version");
    }

    @Override
    public String getDbPath() {
        return dbFile.getPath();
    }

    @Override
    public File getDbFile() {
        return dbFile;
    }

    @Override
    public int getAmountOfChanges() {
        if (currentlyUsedTable == null) {
            return 0;
        } else {
            return currentlyUsedTable.getAmountOfChanges();
        }
    }

    @Override
    public int getTableDimensions() {
        checkTableUsed();
        return currentlyUsedTable.getTableDimensions();
    }

    @Override
    public List<String> list() {
        throw new IllegalStateException("this method is not supported by this implementation");
    }

    @Override
    public String getName() {
        return dbFile.getName();
    }

    @Override
    public String get(String key) {
        checkTableUsed();
        Storeable prepareToGet = currentlyUsedTable.get(key);
        if (prepareToGet == null) {
            return null;
        } else {
            return currentlyUsedTableProvider.serialize(currentlyUsedTable, prepareToGet);
        }
    }

    @Override
    public String put(String key, String value) {
        checkTableUsed();
        Storeable insertValue = null;
        try {
            insertValue = currentlyUsedTableProvider.deserialize(currentlyUsedTable, value);
        } catch (ParseException exc) {
            throw new IllegalArgumentException("wrong format of structured value");
        }
        Storeable resultStoreable = currentlyUsedTable.put(key, insertValue);
        if (resultStoreable == null) {
            return null;
        } else {
            return currentlyUsedTableProvider.serialize(currentlyUsedTable, resultStoreable);
        }
    }

    @Override
    public String remove(String key) {
        checkTableUsed();
        return currentlyUsedTable.remove(key).toString();
    }

    @Override
    public int size() {
        checkTableUsed();
        return currentlyUsedTable.size();
    }

    @Override
    public int commit() {
        checkTableUsed();
        return currentlyUsedTable.commit();
    }

    @Override
    public int rollback() {
        checkTableUsed();
        return currentlyUsedTable.rollback();
    }

    @Override
    public int exit() {
        //FIXME what needs to be done here?
        return 0;
    }

    private void checkTableUsed() {
        if (currentlyUsedTable == null) {
            throw new NoTableException();
        }
    }
}
