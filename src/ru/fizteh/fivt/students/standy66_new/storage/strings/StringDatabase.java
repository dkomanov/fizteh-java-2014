package ru.fizteh.fivt.students.standy66_new.storage.strings;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.standy66_new.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** TableProvider implementation
 * Created by astepanov on 20.10.14.
 */

//TODO: rewrite this bullshit

public class StringDatabase implements TableProvider {
    private File dbDirectory;
    private Map<String, Table> tableInstances;

    public StringDatabase(File directory) {
        if (directory == null) {
            throw new IllegalArgumentException("directory is null");
        }
        dbDirectory = directory;
        tableInstances = new HashMap<>();
    }

    @Override
    public Table getTable(String name) {
        checkTableName(name);
        if (tableInstances.get(name) == null) {
            File tableDirectory = new File(dbDirectory, name);
            if (!tableDirectory.exists()) {
                return null;
            }
            tableInstances.put(name, new StringTable(tableDirectory));
        }
        return tableInstances.get(name);
    }

    @Override
    public Table createTable(String name) {
        checkTableName(name);
        File tableDirectory = new File(dbDirectory, name);
        if (tableDirectory.exists()) {
            return null;
        }
        if (!tableDirectory.mkdirs()) {
            return null;
        }
        tableInstances.put(name, new StringTable(tableDirectory));
        return tableInstances.get(name);
    }

    public Collection<String> listTableNames() {
        Collection<String> tables = new ArrayList<>();
        for (File f : dbDirectory.listFiles()) {
            if (f.getAbsoluteFile().isDirectory()) {
                tables.add(f.getName());
            }
        }
        return tables;
    }

    @Override
    public void removeTable(String name) {
        checkTableName(name);
        File tableDirectory = new File(dbDirectory, name);
        if (!tableDirectory.exists()) {
            throw new IllegalStateException("table doesn't exist");
        }
        FileUtils.deleteRecursively(tableDirectory);
    }

    private void checkTableName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("table name is null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("table name is empty");
        }
        if (name.contains(File.pathSeparator)) {
            throw new IllegalArgumentException("name contains file separator");
        }
    }

    public void commit() {
        for (Table t : tableInstances.values()) {
            t.commit();
        }
    }

    public void rollback() {
        for (Table t: tableInstances.values()) {
            t.rollback();
        }
    }

}
