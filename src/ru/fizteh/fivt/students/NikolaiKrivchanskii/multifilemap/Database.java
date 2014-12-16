package ru.fizteh.fivt.students.NikolaiKrivchanskii.multifilemap;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

public class Database implements TableProvider {
    HashMap<String, MultifileTable> content = new HashMap<String, MultifileTable>();
    private String databaseDirectoryPath;

    public Database(String databaseDirectoryPath) {
        this.databaseDirectoryPath = databaseDirectoryPath;
        File databaseDirectory = new File(databaseDirectoryPath);
        //if (databaseDirectory.getUsableSpace() != 0) {
        	for(File tableFile : databaseDirectory.listFiles()) {
            	if (tableFile!=null || tableFile.isFile()) {
                	continue;
            	}
            	MultifileTable table = new MultifileTable(databaseDirectoryPath, tableFile.getName());
            	content.put(table.getName(), table);
        	}
        //}
    }

    public MultifileTable getTable(String name) throws SomethingIsWrongException {
        if (name == null) {
            throw new SomethingIsWrongException("Table's name cannot be null");
        }
        MultifileTable table = content.get(name);

        if (table == null) {
            throw new SomethingIsWrongException("Tablename does not exist");
        }
        if (table.getChangesCounter() > 0) {
            throw new SomethingIsWrongException("There are " + table.getChangesCounter() + " uncommited changes.");
        }

        return table;
    }

    public MultifileTable createTable(String name) throws SomethingIsWrongException {
        if (name == null) {
            throw new IllegalArgumentException("Table's name cannot be null");
        }
        if (content.containsKey(name)) {
            throw new IllegalStateException("Table already exists");
        }
        MultifileTable table = new MultifileTable(databaseDirectoryPath, name);
        content.put(name, table);
        return table;
    }

    public void removeTable(String name) throws SomethingIsWrongException {
        if (name == null) {
            throw new SomethingIsWrongException("Table's name cannot be null");
        }

        if (!content.containsKey(name)) {
            throw new SomethingIsWrongException("Table doesn't exist");
        }

        content.remove(name);

        File tableFile = new File(databaseDirectoryPath, name);
        tableFile.delete();
    }

	public HashMap<String, Integer> showTables() {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		for (Entry<String, MultifileTable> contents : content.entrySet()) {
			result.put(contents.getKey(), contents.getValue().size());
		}
		return result;
	}
}
