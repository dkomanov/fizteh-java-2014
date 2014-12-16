package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;

import java.io.File;
import java.util.Set;

public class SimpleTableBuilder implements TableBuilder {
	TableUsingStrings table;

	public String get(String key) {
		return table.rawGet(key);
	}

	public void put(String key, String value) {
		table.rawPut(key, value);		
	}

	public Set<String> getKeys() {
		return table.unchangedOldData.keySet();
	}

	public File getTableDirectory() {
		return new File(table.getParentDirectory(), table.getName());
	}

	public void setCurrentFile(File currentFile) {
	}
	
	public SimpleTableBuilder(TableUsingStrings table) {
		this.table = table;
	}


}
