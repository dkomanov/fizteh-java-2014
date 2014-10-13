package ru.fizteh.fivt.students.AndrewTimokhin.FileMap;

import java.util.*;

class Functional {
    private Map<String, String> map;

    public Functional(Map<String, String> map_) {
        map = map_;
    }

    void put(String key, String value) {
        if (map.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(map.get(key));
        } else {
            System.out.println("new");
        }
        map.put(key, value);
    }

    void get(String key) {
        if (map.containsKey(key)) {
            System.out.println("found");
            System.out.println(map.get(key));
        } else {
            System.out.println("not found");
        }
    }

    void remove(String key) {
        if (map.containsKey(key)) {
            map.remove(key);
        } else {
            System.out.println("not found");
        }

    }

    void list() {
        Set<String> st = map.keySet();
        if (st.size() == 0) {
            System.out.println();
        } else {
            for (String key : st) {
                System.out.println(key);
            }
        }
    }
}
