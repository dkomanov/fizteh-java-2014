package ru.fizteh.fivt.students.moskupols.parallel;

import java.util.Map;
import java.util.Set;

/**
 * Created by moskupols on 14.12.14.
 */
public interface MapTransaction<K, V> {
    Map<K, V> getPutProxy();
    Set<K> getNewKeysProxy();
    Set<K> getRemovedKeysProxy();
}
