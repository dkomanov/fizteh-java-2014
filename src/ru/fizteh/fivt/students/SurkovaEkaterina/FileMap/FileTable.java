package ru.fizteh.fivt.students.SurkovaEkaterina.FileMap;

import java.io.File;
import java.io.IOException;

public class FileTable extends ATable {

    private static final String DATABASE_FILE_NAME = "db.dat";

    public FileTable(final String directory, final String tableName) {
        super(directory, tableName);
    }

    protected final void load() throws IOException {
        FileMapReader.loadFromFile(getDatabaseFilePath(), data);
    }

    protected final void save() throws IOException {
        FileMapWriter.saveToFile(getDatabaseFilePath(), data.keySet(), data);
    }

    private String getDatabaseFilePath() {
        File databaseFile = new File(getDirectory(), DATABASE_FILE_NAME);
        return databaseFile.getAbsolutePath();
    }
}
