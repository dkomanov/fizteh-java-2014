package ru.fizteh.fivt.students.NikolaiKrivchanskii.multifilemap;

import java.io.IOException;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap.FileMapShellStateInterface;

public interface MultifileMapShellStateInterface<Table, Key, Value> 
       extends FileMapShellStateInterface<Table, Key, Value> {
    Table useTable(String name);
    
    Table createTable(String arguments);
    
    void dropTable(String name) throws IOException;
    
    String getCurrentTableName();
}
