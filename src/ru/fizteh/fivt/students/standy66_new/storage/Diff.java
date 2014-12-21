package ru.fizteh.fivt.students.standy66_new.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
* @author andrew
*         Created by andrew on 30.11.14.
*/
public final class Diff {
    private final Map<String, String> base;
    private final Map<String, String> chaged = new HashMap<>();
    private final Set<String> deleted = new HashSet<>();

    public Diff(Map<String, String> base) {
        this.base = base;
    }

    public void apply() {
        synchronized (base) {
            for (Map.Entry<String, String> entry : chaged.entrySet()) {
                base.put(entry.getKey(), entry.getValue());
            }
            for (String key : deleted) {
                base.remove(key);
            }
        }
        chaged.clear();
        deleted.clear();
    }

    public void clear() {
        chaged.clear();
        deleted.clear();
    }



    public String put(String key, String value) {
        String returnValue = get(key);

        if (deleted.contains(key)) {
            deleted.remove(key);
        }
        chaged.put(key, value);

        return returnValue;
    }

    public String remove(String key) {
        String returnValue = get(key);

        chaged.remove(key);
        if (!deleted.contains(key)) {
            deleted.add(key);
        }
        return returnValue;
    }

    public int diffSize() {
        return chaged.size() + deleted.size();
    }

    public int size() {
        return keySet().size();
    }

    public Set<String> keySet() {
        Set<String> baseSet;
        synchronized (base) {
            baseSet = new HashSet<>(base.keySet());
        }
        for (String key : chaged.keySet()) {
            baseSet.add(key);
        }
        for (String key : deleted) {
            baseSet.remove(key);
        }
        return baseSet;
    }

    public String get(String key) {
        if (deleted.contains(key)) {
            return null;
        }
        if (chaged.containsKey(key)) {
            return chaged.get(key);
        }
        return base.get(key);
    }
}
