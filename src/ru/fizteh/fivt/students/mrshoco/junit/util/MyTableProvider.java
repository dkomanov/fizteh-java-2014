package util;

import java.io.File;

import strings.*;

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
        currentTable = new MyTable(tableFile);
        return currentTable;
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Bad name");
        }

        File tableFile = new File(root, name);
        if (tableFile.exists()) {
            System.out.print("tablename exists");
            return null;
        } else {
            tableFile.mkdir();
            System.out.println("created");
        }
        currentTable = new MyTable(tableFile);
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
}
