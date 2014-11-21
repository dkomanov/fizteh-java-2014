package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JUnitTableProvider implements TableProvider {

    public JUnitTableProvider(String Dir) {
        Directory = Paths.get(Dir);
        usingTable = null;
    }


    public JUnitTable getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("invalid argument");
        }
        String tablePath = Directory.toString() + File.separator + name;
        File tableDir = new File(tablePath);
        if (!tableDir.exists()) {
            return null;
        }
        return new JUnitTable(name, tablePath);
    }

    public JUnitTable createTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("invalid argument");
        }
        String tablePath = Directory.toString() + File.separator + name;
        File tableDir = new File(tablePath);
        if (tableDir.exists()) {
            return null;
        }
        return new JUnitTable(name, tablePath);
    }

    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException("invalid argument");
        }
        String tablePath = Directory.toString() + File.separator + name;
        File tableDir = new File(tablePath);
        if (!tableDir.exists()) {
            throw new IllegalStateException(name + "does not exist");
        }
        Executor.delete(tableDir);
        usingTable = null;
    }

    public void setUsingTable(String tablename) throws Exception {
        JUnitTable newTable = getTable(tablename);
        if (newTable == null) {
            throw new Exception(tablename + " does not exist");
        }
        if (usingTable != null) {
            if (usingTable.getChangesCounter() != 0) {
                throw new Exception(usingTable.getChangesCounter() + " uncommited changes");
            }
        }
        usingTable = newTable;
    }

    public String[] getTableNames() {
        return Directory.toFile().list();
    }

    public JUnitTable getCurrentTable() {
        return usingTable;
    }

    private JUnitTable usingTable;

    private Path Directory;

}
