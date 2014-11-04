package ru.fizteh.fivt.students.torunova.junit;


import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.junit.exceptions.TableNotCreatedException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by nastya on 19.10.14.
 */
public class  Database implements TableProvider{
    public String dbName;
    public Map<String, Table> tables = new HashMap<>();
    public Table currentTable = null;

	@Override
	public int hashCode() {
		return super.hashCode();
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

	@Override
	public ru.fizteh.fivt.storage.strings.Table getTable(String name) {
		if (name == null || Pattern.matches(".*" + File.separator + ".*", name) || name.equals("..") || name.equals(".")) {
			throw new IllegalArgumentException("illegal table name");
		}
		return tables.get(name);
	}

	@Override
	public ru.fizteh.fivt.storage.strings.Table createTable(String tableName) {
		if (tableName == null || Pattern.matches(".*" + File.separator + ".*", tableName) || tableName.equals("..") || tableName.equals("."))
			throw new IllegalArgumentException("illegal table name");
        File table = new File(dbName, tableName);
        String newTableName = table.getAbsolutePath();
        if (!tables.containsKey(tableName)) {
			Table newTable = null;
			try {
				newTable = new Table(newTableName);
			} catch (TableNotCreatedException e) {
				System.err.println("Caught TableNotCreatedException: " + e.getMessage());
			} catch (IncorrectFileException e1) {
				System.err.println("Caught IncorrectFileException: " + e1.getMessage());
			} catch (IOException e2) {
				System.err.println("Caught IOException: " + e2.getMessage());
			}
			tables.put(tableName, newTable);
            return newTable;
        }
        return null;
    }

	@Override
	public void removeTable(String name) {
		if (name == null || Pattern.matches(".*" + File.separator + ".*", name) || name.equals("..") || name.equals(".")) {
			throw new IllegalArgumentException("illegal table name");
		}
        File f = new File(dbName, name);
        if (tables.containsKey(name)) {
            boolean result = removeRecursive(f.getAbsolutePath());
            tables.remove(name);
            if (currentTable != null) {
                if (currentTable.tableName.equals(f.getAbsolutePath())) {
                    currentTable = null;
                }
            }
        } else {
			throw new IllegalStateException("does not exist");
		}
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

    public boolean exit() {
		if (currentTable != null) {
			int numberOfUnsavedChanges = currentTable.countChangedEntries();
			if (numberOfUnsavedChanges != 0) {
				System.err.println(numberOfUnsavedChanges + " unsaved changes");
				return false;
			}
		}
		return true;

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
            for (File item:content) {
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
            if (!dIr.delete()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }


}
