package ru.fizteh.fivt.students.moskupols.parallel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by moskupols on 14.12.14.
 */
public class MapTransactionImpl<K, V> implements MapTransaction<K, V> {

    @Override
    public Map<K, V> getPutProxy() {
        return putProxy;
    }

    @Override
    public Set<K> getNewKeysProxy() {
        return newKeysProxy;
    }

    public Set<K> getRemovedKeysProxy() {
        return removedKeysProxy;
    }

    private final Map<K, V> putProxy;
    private final Set<K> newKeysProxy;
    private final Set<K> removedKeysProxy;

    public MapTransactionImpl() {
        this(new HashMap<>(), new HashSet<>(), new HashSet<>());
    }

    public MapTransactionImpl(Map<K, V> putProxy, Set<K> newKeysProxy, Set<K> removedKeysProxy) {
        this.putProxy = putProxy;
        this.newKeysProxy = newKeysProxy;
        this.removedKeysProxy = removedKeysProxy;
    }
}
