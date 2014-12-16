package ru.fizteh.fivt.students.NikolaiKrivchanskii.multifilemap;

import java.io.IOException;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap.FileMapShellStateInterface;

public interface MultifileMapShellStateInterface<Table, Key, Value> extends FileMapShellStateInterface<Table, Key, Value> {
	public Table useTable(String name);
	
	public Table createTable(String arguments);
	
	public void dropTable(String name) throws IOException;
	
	public String getCurrentTableName();
}
