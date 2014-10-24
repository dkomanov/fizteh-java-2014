package ru.fizteh.fivt.students.torunova.multifilehashmap;

import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.TableNotCreatedException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nastya on 19.10.14.
 */
public class  Database {
    public String dbName;
    public Map<String, Table> tables = new HashMap<>();
    public Table currentTable = null;

    public Database(String name) throws IncorrectDbNameException,
                                        IOException,
                                        TableNotCreatedException,
                                        IncorrectFileException {
        if (name == null) {
            throw new IncorrectDbNameException("Name of database not specified."
                    + "Please,specify it via -Dfizteh.db.dir");
        }
        File db = new File(name).getAbsoluteFile();
        if (!db.exists()) {
            db.mkdirs();
        }
        dbName = db.getAbsolutePath();
        File[]dbTables = db.listFiles();
        for (File table:dbTables) {
            if (table.getAbsoluteFile().isDirectory()) {
                    tables.put(table.getName(), new Table(table.getAbsolutePath()));
            }
        }
    }

    public boolean createTable(String tableName)
                 throws TableNotCreatedException,
                        IOException,
                        IncorrectFileException {
        File table = new File(dbName, tableName);
        String newTableName = table.getAbsolutePath();
        if (!tables.containsKey(tableName)) {
            Table newTable = new Table(newTableName);
            tables.put(tableName, newTable);
            return true;
        }
        return false;
    }

    public boolean dropTable(String tableName) {
        File f = new File(dbName, tableName);
        if (tables.containsKey(tableName)) {
            boolean result = removeRecursive(f.getAbsolutePath());
            tables.remove(tableName);
            if (currentTable != null) {
                if (currentTable.tableName.equals(f.getAbsolutePath())) {
                    currentTable = null;
                }
            }
            return result;
        }
        return false;
    }

    public boolean useTable(String name) {
        if (tables.containsKey(name)) {
            currentTable = tables.get(name);
            return true;
        }
        return false;
    }

    public Map<String, Integer> showTables() {
        Map<String, Integer> tables1 = new HashMap<String, Integer>();
        tables.forEach((name, table)->tables1.put(name, table.numberOfEntries));
        return tables1;
    }

    public void close() throws IOException {
        for (Table t :tables.values()) {
            t.commit();
        }
    }
    /**
     * removes file.
     * @param file - filename.
     * @return
     */
    private  boolean remove(final String file) {
        File file1 = new File(file).getAbsoluteFile();
        if (file1.isFile()) {
            if (!file1.delete()) {
                return false;
            }
        } else if (file1.isDirectory()) {
            return false;
        } else if (!file1.exists()) {
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
        File dIr = new File(dir).getAbsoluteFile();
        if (dIr.isDirectory()) {
            if (dir.equals(System.getProperty("user.dir"))) {
                System.setProperty("user.dir", dIr.getParent());
            }
            File[] content = dIr.listFiles();
            for (int i = 0; i < content.length; i++) {
                if (content[i].isDirectory()) {
                    if (!removeRecursive(content[i].getAbsolutePath())) {
                        return false;
                    }
                } else {
                    if (!remove(content[i].getAbsolutePath())) {
                        return false;
                    }
                }
            }
            if (!dIr.delete()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

}
