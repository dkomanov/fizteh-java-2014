package ru.fizteh.fivt.students.NikolaiKrivchanskii.multifilemap;

import java.io.File;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap.GlobalUtils;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap.SimpleTableBuilder;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap.TableUsingStrings;

public class MultiFileMapTableBuilder extends SimpleTableBuilder {
	private int currentDir;
	private int currentFile;
	
	
	public MultiFileMapTableBuilder(TableUsingStrings table) {
		super(table);
	}
	
	public void setCurrentFile(File file) {
		currentDir = GlobalUtils.parseDirNumber(file.getParentFile());
		currentFile = GlobalUtils.parseFileNumber(file);
	}
	
	public void put(String key, String value) {
		GlobalUtils.checkKeyPlacement(key, currentDir, currentFile);
		super.put(key, value);
	}
}
