package ru.fizteh.fivt.students.sautin1.filemap;

import ru.fizteh.fivt.storage.strings.Table;

/**
 * Created by sautin1 on 10/11/14.
 */
public interface DiffTable extends Table {
    /**
     * Returns the number of differences between the current table now and the last commit.
     */
    int diffCount();
}
