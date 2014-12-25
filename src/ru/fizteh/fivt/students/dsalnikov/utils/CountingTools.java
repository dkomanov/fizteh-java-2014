package ru.fizteh.fivt.students.dsalnikov.utils;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CountingTools {
    public static int countChangesInFileMap(Map<String, String> data,
                                            Map<String, String> changes, Set<String> removedKeys) {
        int result = 0;
        int notNeedToRewrite = 0;
        for (String key : removedKeys) {
            if (changes.get(key) == null) {
                result++;
            } else if (changes.get(key).equals(data.get(key))) {
                notNeedToRewrite++;
            }
        }
        result += changes.size();
        result -= notNeedToRewrite;
        return result;
    }

    public static int countSize(Map<String, String> data,
                                Map<String, String> changes, Set<String> removedKeys) {
        removedKeys.stream().filter(key -> !data.containsKey(key)).forEach(removedKeys::remove);
        Set<String> changesKeysSet = changes.keySet();
        Set<String> addedKeysForDeletion = new HashSet<>();
        for (String key : changesKeysSet) {
            if (data.containsKey(key)) {
                if (data.get(key).equals(changes.get(key))) {
                    addedKeysForDeletion.add(key);
                    if (removedKeys.contains(key)) {
                        removedKeys.remove(key);
                    }
                } else {
                    if (!removedKeys.contains(key)) {
                        removedKeys.add(key);
                    }
                }
            }
        }
        changes.keySet().removeAll(addedKeysForDeletion);
        return data.size() + changes.size() - removedKeys.size();
    }

    public static boolean equals(Table table, Storeable first, Storeable second) {
        for (Integer i = 0; i < table.getColumnsCount(); ++i) {
            if (first.getColumnAt(i) == null && second.getColumnAt(i) != null) {
                return false;
            }

            if (first.getColumnAt(i) != null && second.getColumnAt(i) == null) {
                return false;
            }

            if (first.getColumnAt(i) == null && second.getColumnAt(i) == null) {
                continue;
            }

            if (!first.getColumnAt(i).equals(second.getColumnAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int countingOfChangesInStoreable(Table table, Map<String, Storeable> data,
                                                   Map<String, Storeable> changes, Set<String> removedKeys) {
        int result = 0;
        int notNeedToRewrite = 0;
        for (String key : removedKeys) {
            if (changes.get(key) == null) {
                result++;
            } else if (equals(table, changes.get(key), data.get(key))) {
                notNeedToRewrite++;
            }
        }
        result += changes.size();
        result -= notNeedToRewrite;
        return result;
    }

    public static int storableCountSize(Map<String, Storeable> data,
                                        Map<String, Storeable> changes, Set<String> removedKeys) {
        removedKeys.stream().filter(key -> !data.containsKey(key)).forEach(removedKeys::remove);
        Set<String> changesKeysSet = changes.keySet();
        Set<String> addedKeysForDeletion = new HashSet<>();
        for (String key : changesKeysSet) {
            if (data.containsKey(key)) {
                if (data.get(key).equals(changes.get(key))) {
                    addedKeysForDeletion.add(key);
                    if (removedKeys.contains(key)) {
                        removedKeys.remove(key);
                    }
                } else {
                    if (!removedKeys.contains(key)) {
                        removedKeys.add(key);
                    }
                }
            }
        }
        changes.keySet().removeAll(addedKeysForDeletion);
        return data.size() + changes.size() - removedKeys.size();
    }
}
