package ru.fizteh.fivt.students.elina_denisova.j_unit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MyTableProvider implements TableProvider {

    private HashMap<String, MyTable> tables;
    private String using;
    private File parentDirectory;


    public MyTableProvider(String path) {
        parentDirectory = new File(path);
        using = null;
        tables = new HashMap<>();

        try {
            if (path == null) {
                throw new NullPointerException("MyTableProvider: Wrong path");
            }
            if (!Files.exists(parentDirectory.toPath()) && !parentDirectory.mkdir()) {
                throw new UnsupportedOperationException("MyTableProvider: Cannot create working directory");
            }
            if (!parentDirectory.isDirectory()) {
                throw new FileNotFoundException("MyTableProvider:" + parentDirectory.toString()
                        + " is not a directory");
            }
            for (String childName : parentDirectory.list()) {
                File childDirectory = new File(parentDirectory, childName);
                if (childDirectory.isDirectory()) {
                    tables.put(childName, new MyTable(childDirectory));
                } else {
                    throw new Exception("MyTableProvider:" + childName + " from databases directory is not a directory");
                }
            }
        } catch (UnsupportedOperationException e) {
            HandlerException.handler(e);
        } catch (NullPointerException e) {
            HandlerException.handler(e);
        } catch (FileNotFoundException e) {
            HandlerException.handler(e);
        } catch (Exception e) {
            HandlerException.handler("TableProviderFactory: Unknown error", e);
        }
    }

    @Override
    public Table getTable(String name) {
        return tables.get(name);
    }

    @Override
    public Table createTable(String name) {
        if (name == null) throw new IllegalArgumentException("MyTableProvider.createTable: "
        + "Invalid name. ");
        if (tables.containsKey(name)) {
            return null;
        } else {
            File newTable = new File(parentDirectory, name);
            if (!newTable.mkdir()) {
                throw new UnsupportedOperationException("MyTableProvider.createTable: "
                        + "Unable to create working directory for new table. ");
            }
            tables.put(name, new MyTable(newTable));
            return tables.get(name);
        }
    }

    @Override
    public void removeTable(String name) {
        if (name == null) throw new IllegalArgumentException("MyTableProvider.removeTable: "
                + "Invalid name. ");
        if (tables.containsKey(name)) {
            File table = new File(parentDirectory, name);
            for (int i = 0; i < 16; i++) {
                File subDir = new File(table, i + ".dir");
                for (int j = 0; j < 16; j++) {
                    String adds = Integer.toString(i * 100 + j);
                    if (tables.get(name).containsKey(adds)) {
                        File dbFile = new File(subDir, j + ".dat");
                        if (dbFile.exists()) {
                            try {
                                Files.delete(dbFile.toPath());
                            } catch (IOException e) {
                                throw new RuntimeException("MyTableProvider.removeTable: cannon delete database file", e);
                            }
                        }
                    }
                }
                if (subDir.exists()) {
                    try {
                        Files.delete(subDir.toPath());
                    } catch (DirectoryNotEmptyException e) {
                        throw new RuntimeException("MyTableProvider.removeTable: cannot remove table subdirectory", e);
                    } catch (IOException e) {
                        throw new RuntimeException("MyTableProvider.removeTable: cannot delete database subdirectory", e);
                    }
                }
            }
            try {
                Files.delete(table.toPath());
            } catch (DirectoryNotEmptyException e) {
                throw new RuntimeException("TableProvider.tDrop: cannot remove main table directory", e);
            } catch (IOException e) {
                throw new RuntimeException("TableProvider.tDrop: cannot delete main database directory", e);
            }

            tables.remove(name);
        }
        else throw new IllegalStateException("MyTableProvider.removeTable: " + name + "doesn't exist");

    }

    public Table getUsing() {
        return tables.get(using);
    }

    public void changeUsingTable(String table) {
        using = table;
    }


    public Set<Map.Entry<String, MyTable>> entrySet() {
        return tables.entrySet();
    }


    public boolean containsKey(String key) {
        return tables.containsKey(key);
    }

}
