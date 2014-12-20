package ru.fizteh.fivt.students.torunova.proxy.database;


import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.torunova.proxy.database.exceptions.IncorrectDbException;
import ru.fizteh.fivt.students.torunova.proxy.database.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.proxy.database.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.proxy.database.exceptions.TableNotCreatedException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by nastya on 19.10.14.
 */
public class  Database implements TableProvider {
    private  String dbName;
    private  Map<String, TableImpl> tables = new HashMap<>();

    @Override
    public int hashCode() {
        return dbName.hashCode();
    }

    @Override
    public boolean equals(Object db1) {
        if (!(db1 instanceof Database)) {
            return false;
        }
        Database db = (Database) db1;
        return dbName.equals(db.dbName);
    }

    public Database(String name) throws IncorrectDbNameException,
                                        IOException,
            TableNotCreatedException,
            IncorrectFileException,
            IncorrectDbException {
        File db = new File(name).getAbsoluteFile();
        if (!db.exists()) {
            db.mkdirs();
        } else if (!db.isDirectory()) {
            throw new IncorrectDbNameException("File with this name already exists.");
        }
        dbName = db.getAbsolutePath();
        File[]dbTables = db.listFiles();
        if (dbTables != null) {
            for (File table : dbTables) {
                if (table.getAbsoluteFile().isDirectory()) {
                    tables.put(table.getName(), new TableImpl(table.getAbsolutePath()));
                } else {
                    throw new IncorrectDbException("Database contains illegal files.");
                }
            }
        }
    }

    @Override
    public TableImpl getTable(String name) {
        checkTableName(name);
        return tables.get(name);
    }

    @Override
    public TableImpl createTable(String tableName) {
        checkTableName(tableName);
        File table = new File(dbName, tableName);
        String newTableName = table.getAbsolutePath();
        if (!tables.containsKey(tableName)) {
            TableImpl newTable;
            try {
                newTable = new TableImpl(newTableName);
            } catch (TableNotCreatedException | IncorrectFileException | IOException e) {
                throw new RuntimeException(e);
            }
            tables.put(tableName, newTable);
            return newTable;
        }
        return null;
    }

    @Override
    public void removeTable(String name) {
        checkTableName(name);
        File f = new File(dbName, name);
        if (tables.containsKey(name)) {
            removeRecursive(f.getAbsolutePath());
            tables.get(name).markAsRemoved();
            tables.remove(name);
        } else {
            throw new IllegalStateException("does not exist");
        }
    }

    public Map<String, Integer> showTables() {
        Map<String, Integer> tablesWithSize = new HashMap<>();
        tables.forEach((name, table)->tablesWithSize.put(name, table.getNumberOfEntries()));
        return tablesWithSize;
    }
    public String getDbName() {
        return dbName;
    }

    public void close() {
        for (TableImpl t :tables.values()) {
            t.rollback();
            t.markAsClosed();
        }
    }
    /**
     * removes file
     * @param file - filename.
     * @return true if file is regular and deleted,false otherwise.
     */
    private  boolean remove(final String file) {
        File fileWithAbsolutePath = new File(file).getAbsoluteFile();
        if (fileWithAbsolutePath.isFile()) {
            if (!fileWithAbsolutePath.delete()) {
                return false;
            }
        } else if (fileWithAbsolutePath.isDirectory()) {
            return false;
        } else if (!fileWithAbsolutePath.exists()) {
            return false;
        }
        return true;
    }
    /**
     * removes directory.
     *
     * @param dir - directory name.
     */
    private  boolean removeRecursive(final String dir) {
        File directory = new File(dir).getAbsoluteFile();
        if (directory.isDirectory()) {
            File[] content = directory.listFiles();
            if (content != null) {
                for (File item : content) {
                    if (item.isDirectory()) {
                        if (!removeRecursive(item.getAbsolutePath())) {
                            return false;
                        }
                    } else {
                        if (!remove(item.getAbsolutePath())) {
                            return false;
                        }
                    }
                }
            }
            if (!directory.delete()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
    private void checkTableName(String name) {
        if (name == null || Pattern.matches(".*" + Pattern.quote(File.separator) + ".*", name)
                || name.equals("..") || name.equals(".")) {
            throw new IllegalArgumentException("illegal table name");
        }
    }


}
