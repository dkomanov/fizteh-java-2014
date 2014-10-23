package ru.fizteh.fivt.students.torunova.multifilehashmap;

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
	public Database(String name) {
		File db = new File(name).getAbsoluteFile();
		if(!db.exists()) {
			db.mkdirs();
		}
		dbName = db.getAbsolutePath();
		File[]dbTables = db.listFiles();
		for(File f:dbTables) {
			if(f.getAbsoluteFile().isDirectory()){
				try {
					tables.put(f.getName(), new Table(f.getAbsolutePath()));
				} catch(TableNotCreatedException e) {
					System.err.println("Caught TableNotCreatedException");
				} catch (IncorrectFileException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public boolean createTable(String tableName) throws TableNotCreatedException, IOException, IncorrectFileException {
		File f = new File(dbName,tableName);
		String newTableName = f.getAbsolutePath();
		if(!tables.containsKey(tableName)) {
			Table newTable = new Table(newTableName);
			tables.put(tableName,newTable);
			return true;
		}
		return false;
	}
	public boolean dropTable(String tableName) {
		File f = new File(dbName,tableName);
		if(tables.containsKey(tableName)) {
			boolean result = removeRecursive(f.getAbsolutePath());
			tables.remove(tableName);
			if (currentTable.tableName.equals(f.getAbsolutePath())) {
				currentTable = null;
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
		tables.forEach((name,table)->tables1.put(name,table.numberOfEntries));
		return tables1;
	}
	public void close() throws IOException {
		for(Table t :tables.values()) {
			t.commit();
		}
	}
	/**
	 * removes file.
	 * @param file - filename.
	 * @return
	 */
	private  boolean remove(final String file) {
		File f = new File(file).getAbsoluteFile();
		if (f.isFile()) {
			if (!f.delete()) {
				return false;
			}
		} else if (f.isDirectory()) {
			return false;
		} else if (!f.exists()) {
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
