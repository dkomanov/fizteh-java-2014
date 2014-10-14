package ru.fizteh.fivt.students.isalysultan.FileMap;

import java.util.Iterator;
import java.util.Set;

public class Commands {
    void put(String key, String value, Table tables) {
        boolean newElement = false;
        if (!tables.storage.containsKey(key)) {
            System.out.println("new");
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

    void get(String key, Table tables) {
        String result = tables.storage.get(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(result);
        }
    }

    void remove(String key, Table tables) {
        String result = tables.storage.remove(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    void list(Table tables) {
        Set<String> result = tables.storage.keySet();
        Iterator<String> it = result.iterator();
        String answer = String.join(", ", result);
        System.out.println(answer);
    }
}

