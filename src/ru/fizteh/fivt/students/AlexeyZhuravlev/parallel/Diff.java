package ru.fizteh.fivt.students.AlexeyZhuravlev.parallel;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Table;

import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */

public class Diff {
    HashMap<String, String> creations;
    HashMap<String, String> deletions;
    HashMap<String, String> overwrites;
    Table origin;

    public Diff(Table passedTable) {
        origin = passedTable;
        creations = new HashMap<>();
        deletions = new HashMap<>();
        overwrites = new HashMap<>();
    }
}
