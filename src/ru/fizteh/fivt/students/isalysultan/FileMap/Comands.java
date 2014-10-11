package ru.fizteh.fivt.students.IsalySultan.FileMap;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class Comands {
    void put(String key, String value, Table tables) {
        boolean flagvalue = false;
        if (!tables.storage.containsValue(value)) {
            System.out.println("new");
            flagvalue = true;
        }
        String result = tables.storage.put(key, value);
        if (result == null) {
            return;
        } else {
            if ((result.equals(value))) {
                return;
            } else if (flagvalue) {
                System.out.println("overwrite");
                System.out.println(result);
            }
            return;
        }
    }

    void get(String key, Table tables) {
        String result = tables.storage.get(key);
        if (result == null) {
            System.out.println("not found");
            return;
        } else {
            System.out.println("found");
            System.out.println(result);
            return;
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
        String answer = result.stream().collect(Collectors.joining(", "));
        System.out.print(answer);
    }
}
