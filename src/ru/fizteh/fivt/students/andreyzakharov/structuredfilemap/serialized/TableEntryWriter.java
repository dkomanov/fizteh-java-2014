package ru.fizteh.fivt.students.andreyzakharov.structuredfilemap.serialized;

import ru.fizteh.fivt.storage.structured.Storeable;

public interface TableEntryWriter {
    String serialize(Storeable data);
}
