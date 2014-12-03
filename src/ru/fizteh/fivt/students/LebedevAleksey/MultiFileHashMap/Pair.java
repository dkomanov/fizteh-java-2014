package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

public class Pair<K, V> {
    private K first;
    private V second;

    public Pair(K key, V value) {
        first = key;
        second = value;
    }

    public K getKey() {
        return first;
    }

    public V getValue() {
        return second;
    }
}
