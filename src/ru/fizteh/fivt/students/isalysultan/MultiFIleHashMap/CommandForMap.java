package ru.fizteh.fivt.students.isalysultan.MultiFileHashMap;

import java.util.Iterator;
import java.util.Set;

public class CommandForMap {
    public static void put(String key, String value, FileTable tables,
            Table mainTable) {
        boolean newElement = false;
        if (!tables.storage.containsKey(key)) {
            System.out.println("new");
            mainTable.incrementNumberRecords();
        }
        if (!tables.storage.containsValue(value)) {
            newElement = true;
        }
        String result = tables.storage.put(key, value);
        if (result == null) {
            return;
        } else if (newElement && !result.equals(value)) {
            System.out.println("overwrite");
            System.out.println(result);
        }
    }

    public static void get(String key, FileTable tables) {
        String result = tables.storage.get(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(result);
        }
    }

    public static void remove(String key, FileTable tables) {
        String result = tables.storage.remove(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public static void list(FileTable tables) {
        Set<String> result = tables.storage.keySet();
        Iterator<String> it = result.iterator();
        String answer = String.join(", ", result);
        System.out.println(answer);
    }
}
