package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.text.ParseException;
import java.util.List;

/**
 * Created by moskupols on 09.12.14.
 */
public interface Deserializer {
    Storeable deserialize(List<StoreableAtomType> signature, String value) throws ParseException;
}
