package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;

public interface FileMapShellStateInterface<Table, Key, Value> {

	public Value put (Key key, Value value);
	
	public Value get (Key key);
	
	public int commit();
	
	public int rollback();
	
	public int size();
	
	public Value remove(Key key);
	
	public Table getTable();
	
	public String keyToString(Key key);
	
	public String valueToString(Value value);
	
	public Key parseKey(String key);
	
	public Value parseValue(String value);
}
