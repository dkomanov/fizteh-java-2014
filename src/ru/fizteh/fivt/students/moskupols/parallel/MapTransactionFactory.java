package ru.fizteh.fivt.students.moskupols.parallel;

/**
 * Created by moskupols on 14.12.14.
 */
public interface MapTransactionFactory<K, V> {
    MapTransaction<K, V> create();
}
