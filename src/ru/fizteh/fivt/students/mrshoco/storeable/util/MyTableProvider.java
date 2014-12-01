package util;

import java.io.File;
import java.util.List;

import structured.*;

public class MyTableProvider implements TableProvider{
    File root;
    MyTable currentTable;

    public MyTableProvider(File file) {
        root = file;
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Given property isn't a directory");
        }
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Bad name");
        }

        File tableFile = new File(root, name);
        if (!tableFile.isDirectory()) {
            System.out.println("table not exists");
            return null;
        } else {
            if (currentTable != null && currentTable.diff() != 0) {
                System.out.println(currentTable.diff() + " unsaved changes");
                throw new IllegalArgumentException(currentTable.diff() + " unsaved changes");
            }
                System.out.println("using " + name);
        }
        currentTable = new MyTable(tableFile, this);
        return currentTable;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) {
        if (name == null) {
            throw new IllegalArgumentException("Bad name");
        }

        File tableFile = new File(root, name);
        if (tableFile.exists()) {
            System.out.println("tablename exists");
            return null;
        } else {
            tableFile.mkdir();
            System.out.println("created");
        }
        currentTable = new MyTable(tableFile, this);
        return currentTable;
    }
    
    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Bad name");
        }

        File tableFile = new File(root, name);
        if (!tableFile.isDirectory()) {
            System.out.println("tablename not exist");
            throw new IllegalStateException("Table doesn't exist");
        } else {
            try {
                for (int i = 0; i < 16; i++) {
                    File folder = new File(tableFile, i + ".dir");
                    if (folder.exists()) {
                        for (int j = 0; j < 16; j++) {
                            File file = new File(folder, j + ".dat");
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                        folder.delete();
                    }
                }
                tableFile.delete();
                System.out.println("dropped");
            } catch (Exception e) {
                throw new IllegalArgumentException("Some of directories is not empty");
            }
        }
    }
    
    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
    }

    @Override
    public Storeable createFor(Table table) {
    }

    @Override
    public Storeable createFor(Table table, List<?> values) 
                                throws ColumnFormatException, IndexOutOfBoundsException {
    }

    @Override
    public List<String> getTableNames() {
    }

}
