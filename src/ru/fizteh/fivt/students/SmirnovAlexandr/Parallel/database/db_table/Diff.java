package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.db_table;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Diff {
    // Important invariant: uncommittedChangesMap doesn't intersect uncommittedDeletionsSet!
    public Map<String, String> changesMap = new HashMap<>();
    public Set<String> deletionsSet = new HashSet<>();
}
