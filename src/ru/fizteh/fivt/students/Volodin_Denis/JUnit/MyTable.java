package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.strings.Table;

public class MyTable implements Table {

    private String dbPath;
    private DataBase dbData;
    
    MyTable(final String path) throws Exception {
        dbPath = Paths.get(path).toAbsolutePath().normalize().toString();
        dbData = new DataBase(dbPath);
    }

    @Override
    public String getName() {
        return Paths.get(dbPath).getFileName().toString();
    }

    @Override
    public String get(final String key) {
        String value = dbData.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
        return value;
    }

    @Override
    public String put(String key, String value) {
        String oldValue = dbData.put(key, value);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
        return oldValue;
    }

    @Override
    public String remove(String key) {
        String value = dbData.remove(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
        return value;
    }

    @Override
    public int size() {
       return dbData.size();
    }

    @Override
    public int numUncommitedChanges() {
        return dbData.numUncommitedChanges();
    }
    
    @Override
    public int commit() {
        int changes = 0;
        if (dbData == null) {
            System.out.println("no table");
            return 0;
        }
        System.out.println(changes);
        try {
            dbData.upgrade();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return changes;
    }

    @Override
    public int rollback() {
        if (dbData == null) {
            System.out.println("no table");
            return 0;
        }
        int changes = dbData.numUncommitedChanges();
        System.out.println(changes);
        dbData.downgrade();
        return changes;
    }

    @Override
    public List<String> list() {

        List<String> keys = new ArrayList<String>();
        for (String key : dbData.keySet()) {
            keys.add(key);
        }
        return keys;
    }

}
