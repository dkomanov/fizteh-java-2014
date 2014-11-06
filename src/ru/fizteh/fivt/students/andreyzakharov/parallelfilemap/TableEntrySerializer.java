package ru.fizteh.fivt.students.andreyzakharov.parallelfilemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;

public interface TableEntrySerializer {
    Storeable deserialize(Table table, String value) throws ParseException;

    String serialize(Table table, Storeable value);
}
