package ru.fizteh.fivt.students.isalysultan.MultiFileHashMap;

import java.util.Iterator;
import java.util.Set;

public class CommandForMap {
    public static void put(String key, String value, FileTable tables,
            Table mainTable) {
        boolean newElement = false;
        if (!tables.ContainsKey(key)) {
            System.out.println("new");
            mainTable.incrementNumberRecords();
        }
        if (!tables.ContainsValue(value)) {
            newElement = true;
        }
        String result = tables.putMap(key, value);
        if (result == null) {
            return;
        } else if (newElement && !result.equals(value)) {
            System.out.println("overwrite");
            System.out.println(result);
        }
    }

    public static void get(String key, FileTable tables) {
        String result = tables.getForMap(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(result);
        }
    }

    public static void remove(String key, FileTable tables, Table Data) {
        String result = tables.removeMap(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
            Data.dicrementNumberRecords();
        }
    }

    public static void list(FileTable tables) {
        if (tables != null) {
            Set<String> result = tables.keySetMap();
            Iterator<String> it = result.iterator();
            String answer = String.join(", ", result);
            System.out.println(answer);
        } else {
            return;
        }
    }
}
