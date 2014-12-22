package ru.fizteh.fivt.students.SibgatullinDamir.junit;

import java.nio.file.Paths;

/**
 * Created by Lenovo on 09.11.2014.
 */
public class MyTableProvider implements TableProvider {

    private String directory;

    protected MyTableProvider(String path) {
        try {
            DatabaseJUnit.database = new MultiFileHashMap(Paths.get(path));
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (DatabaseJUnit.database.containsKey(name)) {
            return new MyTable(DatabaseJUnit.database.get(name));
        } else {
            return null;
        }
    }

    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (DatabaseJUnit.database.containsKey(name)) {
            return null;
        } else {
            CreateCommand create = new CreateCommand();
            String[] args = {"create", name};
            try {
                create.execute(args, DatabaseJUnit.database);
            } catch (Exception e) {
                throw new RuntimeException();
            }
            return new MyTable(DatabaseJUnit.database.get(name));
        }
    }

    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (!DatabaseJUnit.database.containsKey(name)) {
            throw new IllegalStateException();
        }
        DropCommand drop = new DropCommand();
        String[] args = {"drop", name};
        try {
            drop.execute(args, DatabaseJUnit.database);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }
}
