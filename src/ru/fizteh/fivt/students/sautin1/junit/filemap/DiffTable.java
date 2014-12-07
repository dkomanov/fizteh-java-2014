package ru.fizteh.fivt.students.sautin1.junit.filemap;

import ru.fizteh.fivt.storage.strings.Table;

/**
 * Table with ability to count changes.
 * Created by sautin1 on 10/11/14.
 */
public interface DiffTable extends Table {
    /**
     * Returns the the number of changes made to the table since the last commit.
     */
    int diffCount();
}
