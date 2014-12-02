package ru.fizteh.fivt.students.LevkovMiron.JUnit;

import java.io.File;

/**
 * Created by Мирон on 24.10.2014 ru.fizteh.fivt.students.LevkovMiron.JUnit.
 */
public class TableProviderClass implements TableProvider{

    File parentDirectory;
    TableProviderClass(String dir) throws IllegalArgumentException {
        try {
            parentDirectory = new File(dir);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        if (!parentDirectory.exists() || parentDirectory.isFile()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public TableClass getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        File tableFile = new File(parentDirectory.getAbsolutePath() + "/" + name);
        if (!tableFile.exists()) {
            return null;
        }
        if (tableFile.isFile()) {
            throw new IllegalArgumentException();
        }
        return new TableClass(parentDirectory, tableFile);
    }
    @Override
    public TableClass createTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        File tableFile = new File(parentDirectory.getAbsolutePath() + "/" + name);
        if (tableFile.exists()) {
            return null;
        }
        tableFile.mkdir();
        return new TableClass(parentDirectory, tableFile);
    }
    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        File tableFile = new File(parentDirectory.getAbsolutePath() + "/" + name);
        if (!tableFile.exists()) {
            throw new IllegalStateException();
        }
        TableClass table = new TableClass(parentDirectory, tableFile);
        table.drop(tableFile);
        removeFile(tableFile);
    }
    void removeFile(File deletedFile) {
        if (!deletedFile.isDirectory()) {
            deletedFile.delete();
            return;
        }
        for (File f : deletedFile.listFiles()) {
            removeFile(f);
        }
        deletedFile.delete();
    }
}
