package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;

/**
 * Created by moskupols on 09.12.14.
 */
public interface Serializer {
    String serialize(List<StoreableAtomType> signature, Storeable stor);
}
