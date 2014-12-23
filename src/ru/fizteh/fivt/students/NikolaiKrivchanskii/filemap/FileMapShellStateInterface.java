package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;


public interface FileMapShellStateInterface<Table, Key, Value> {

      Value put(Key key, Value value);
    
      Value get(Key key);
    
      int commit();
    
      int rollback();
    
      int size();
    
      Value remove(Key key);
    
      Table getTable();
    
      String keyToString(Key key);
    
      String valueToString(Value value);
    
      Key parseKey(String key);
    
      Value parseValue(String value);
}
