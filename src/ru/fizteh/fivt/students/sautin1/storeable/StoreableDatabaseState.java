package ru.fizteh.fivt.students.sautin1.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.storeable.filemap.DatabaseState;

/**
 * Class stores data which represents the current state of string database.
 * It is passed through shell and commands.
 * Created by sautin1 on 12/11/14.
 */
public class StoreableDatabaseState extends DatabaseState<Storeable, StoreableTable, StoreableTableProvider> {
    public StoreableDatabaseState(StoreableTableProvider tableProvider) {
        super(tableProvider);
    }
}
