package ru.fizteh.fivt.students.standy66_new.storage.strings;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.standy66_new.exceptions.CheckedExceptionCaughtException;
import ru.fizteh.fivt.students.standy66_new.utility.FileUtility;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** TableProvider implementation
 * Created by astepanov on 20.10.14.
 */
public class StringDatabase implements TableProvider, AutoCloseable {
    private static final boolean FILE_BASED_LOCK_MECHANISM = (System.getProperty("use_locks") != null);
    private final File dbDirectory;
    private final Map<String, StringTable> tableInstances;
    private File lockFile;
    private boolean closed = false;

    StringDatabase(File directory) {
        if (directory == null) {
            throw new IllegalArgumentException("directory is null");
        }
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IllegalArgumentException("directory wasn't created");
            }
        }
        if (!directory.canRead()) {
            throw new IllegalStateException("dir cannot be read");
        }
        if (directory.isFile()) {
            throw new IllegalArgumentException("directory is a regular file");
        }
        lockFile = new File(directory, "db.lock");
        if (directory.canWrite()) {
            if (lockFile.exists() && FILE_BASED_LOCK_MECHANISM) {
                throw new IllegalStateException("Database locked");
            }
            try {

                lockFile.createNewFile();
            } catch (IOException e) {
                throw new CheckedExceptionCaughtException("IOException occurred", e);
            }
        }
        dbDirectory = directory;

        tableInstances = Collections.synchronizedMap(new HashMap<>());

        initTableInstances(directory);
    }

    private static void throwIfIncorrectTableName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("table name should not be null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("table name should not be empty");
        }
        if (name.contains(File.pathSeparator)) {
            throw new IllegalArgumentException("table name should not contain file separator");
        }

        if ("..".equals(name) || ".".equals(name)) {
            throw new IllegalArgumentException("table name should not be . or ..");
        }
    }

    public File getFile() {
        return dbDirectory;
    }

    @Override
    public StringTable getTable(String name) {
        assertNotClosed();
        throwIfIncorrectTableName(name);
        return tableInstances.get(name);
    }

    @Override
    public StringTable createTable(String name) {
        assertNotClosed();
        throwIfIncorrectTableName(name);
        File tableDirectory = new File(dbDirectory, name);
        synchronized (tableInstances) {
            if (tableInstances.get(name) != null) {
                return null;
            }
            if (!tableDirectory.mkdirs()) {
                throw new IllegalArgumentException("table cannot be created");
            }
            tableInstances.put(name, new StringTable(tableDirectory, this));
        }
        return tableInstances.get(name);
    }

    public List<String> listTableNames() {
        assertNotClosed();
        synchronized (tableInstances) {
            return tableInstances.values().stream()
                    .map(Table::getName).collect(Collectors.toList());
        }
    }

    @Override
    public void removeTable(String name) {
        assertNotClosed();
        throwIfIncorrectTableName(name);
        synchronized (tableInstances) {
            if (tableInstances.get(name) == null) {
                throw new IllegalStateException("table doesn't exist");
            }
            tableInstances.remove(name);
            if (!FileUtility.deleteRecursively(new File(dbDirectory, name))) {
                throw new IllegalArgumentException("failed to remove table");
            }
        }
    }

    public void commit() {
        assertNotClosed();
        synchronized (tableInstances) {
            tableInstances.values().forEach(Table::commit);
        }
    }

    public void rollback() {
        assertNotClosed();
        synchronized (tableInstances) {
            tableInstances.values().forEach(Table::rollback);
        }
    }

    @Override
    public void close() {
        if (!closed) {
            rollback();
            lockFile.delete();
            closed = true;
        }
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), dbDirectory.getAbsolutePath());
    }

    void onTableClosed(StringTable table) {
        tableInstances.put(table.getName(), new StringTable(table.getFile(), this));
    }

    private void initTableInstances(File directory) {
        if (directory.listFiles() != null) {
            for (File tableFile : directory.listFiles()) {
                if (tableFile.isDirectory()) {
                    String tableName = tableFile.getName();
                    try {
                        throwIfIncorrectTableName(tableName);
                    } catch (RuntimeException e) {
                        throw new CheckedExceptionCaughtException("Database contains incorrect table name: "
                                + e.getMessage(), e);
                    }

                    tableInstances.put(tableName, new StringTable(tableFile, this));
                }
            }
        } else {
            throw new IllegalArgumentException("illegal arg");
        }
    }

    private void assertNotClosed() {
        if (closed) {
            throw new IllegalStateException("String Database had been closed, but then method called");
        }
    }
}
