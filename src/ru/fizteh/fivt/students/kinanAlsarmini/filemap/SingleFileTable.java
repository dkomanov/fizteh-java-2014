package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.*;

import java.io.*;
import java.io.File;

public class SingleFileTable extends StringTable {
    private String databaseFileName;

    private static String getParent(String filePath) {
        File file = (new File(filePath)).getAbsoluteFile();

        if (file.getParent() == null) {
            throw new IllegalArgumentException(String.format("'%s' does not have a parent", filePath));
        }

        return file.getParent();
    }

    public SingleFileTable(String databaseFilePathName, String tableName) {
        super(getParent(databaseFilePathName), tableName);
        this.databaseFileName = (new File(databaseFilePathName)).getName();

        try {
            load();
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid file format");
        }
    }

    protected void load() throws IOException {
        FilemapReader.loadFromFile(getDatabaseFilePath(), new SimpleTableBuilder(this));
    }

    protected void save() throws IOException {
        FilemapWriter.saveToFile(getDatabaseFilePath(), oldData.keySet(), new SimpleTableBuilder(this));
    }

    private String getDatabaseFilePath() {
        File databaseFile = new File(getDirectory(), databaseFileName);

        return databaseFile.getAbsolutePath();
    }
}
