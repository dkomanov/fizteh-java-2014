package ru.fizteh.fivt.students.dsalnikov.storable;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TransactionChanges {


    private ThreadLocal<Map<String, Storeable>> changes;
    private ThreadLocal<Set<String>> removed;
    private ThreadLocal<Integer> changesCounter;
    private StorableTable linkToStorableTable;

    public TransactionChanges() {
        changes = new ThreadLocal<>().withInitial(HashMap::new);
        removed = new ThreadLocal<>().withInitial(HashSet::new);
        changesCounter = new ThreadLocal<>().withInitial(() -> 0);
    }

    public Storeable getFromTransaction(String key) {
        return changes.get().get(key);
    }

    public boolean isKeyInTransaction(String key) {
        return changes.get().containsKey(key);
    }

    public boolean isKeyWasRemovedFromTransaction(String key) {
        return removed.get().contains(key);

    }

    public void removeFromTransaction(String key) {
        changes.get().remove(key);
    }

    public void addKeyToRemovedKeySet(String key) {
        removed.get().add(key);
    }

    public int incrementChangesCounter() {
        changesCounter.set(changesCounter.get() + 1);
        return changesCounter.get();
    }

    public int getChangesCounter() {
        return changesCounter.get();
    }

    public int decrementChangesCounter() {
        changesCounter.set(changesCounter.get() - 1);
        return changesCounter.get();
    }

    public Storeable putIntoTransaction(String key, Storeable value) {
        return changes.get().put(key, value);
    }

    public void setStorage(StorableTable table) {
        linkToStorableTable = table;
    }

    public Set<String> getRawRemoved() {
        return removed.get();
    }

    public Map<String, Storeable> getRawTransacttionChanges() {
        return changes.get();
    }

    public void setDefault() {
        changes.get().clear();
        removed.get().clear();
        changesCounter.set(0);
    }
}
