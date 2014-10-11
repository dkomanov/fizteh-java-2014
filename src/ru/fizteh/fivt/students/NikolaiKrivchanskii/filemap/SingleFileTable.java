package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;


import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.SomethingIsWrongException;


public class SingleFileTable extends SomeTable {
    
    public static final String DATABASENAME = "db.dat";

    public SingleFileTable(String dir, String name) {
        super(dir, name);
    }
    
    protected void load() throws SomethingIsWrongException {
        scanFromDisk(getPathToDatabase());
    }
    
    protected void save() throws SomethingIsWrongException {
        writeOnDisk(unchangedOldData.keySet(), getPathToDatabase());
    }
    
    private String getPathToDatabase() {
    	File curDir = new File(new File(".").getAbsolutePath());
        File databaseFile;
		try {
			databaseFile = new File (curDir.getCanonicalPath(), DATABASENAME);
	        return databaseFile.getAbsolutePath();
		} catch (IOException e) {
			e.getMessage();
		}
		return "";
    }
    

}
