package ru.fizteh.fivt.students.AlexeyZhuravlev.parallel;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTable;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author AlexeyZhuravlev
 */

public class Diff {
    HashMap<String, Storeable> creations;
    HashSet<String> deletions;
    HashMap<String, Storeable> overwrites;
    StructuredTable origin;

    public Diff(StructuredTable passedTable) {
        origin = passedTable;
        creations = new HashMap<>();
        deletions = new HashSet<>();
        overwrites = new HashMap<>();
    }

    public Storeable put(String key, Storeable value) {
        if  (creations.containsKey(key)) {
            return creations.replace(key, value);
        } else if (overwrites.containsKey(key)) {
            return overwrites.replace(key, value);
        } else if (deletions.contains(key)) {
            deletions.remove(key);
            overwrites.put(key, value);
            return null;
        } else {
            // *** обращение к базе
            Storeable originResult = origin.get(key);
            // обращение к базе ***
            if (originResult == null) {
                creations.put(key, value);
            } else {
                overwrites.put(key, value);
            }
            return originResult;
        }
    }

    public Storeable remove(String key) {
        if (creations.containsKey(key)) {
            return creations.remove(key);
        } else if (overwrites.containsKey(key)) {
            return overwrites.remove(key);
        } else if (deletions.contains(key)) {
            return null;
        } else {
            // *** обращение к базе
            Storeable originResult = origin.get(key);
            // обращение к базе ***
            if (originResult != null) {
                deletions.add(key);
            }
            return originResult;
        }
    }

    public Storeable get(String key) {
        if (creations.containsKey(key)) {
            return creations.get(key);
        } else if (overwrites.containsKey(key)) {
            return overwrites.get(key);
        } else if (deletions.contains(key)) {
            return null;
        } else {
            // *** обращение к базе
            Storeable originResult = origin.get(key);
            // обращение к базе ***
            return originResult;
        }
    }

    public int deltaSize() {
        return creations.size() - deletions.size();
    }
}
