package ru.fizteh.fivt.students.moskupols.parallel;

import ru.fizteh.fivt.storage.structured.TableProvider;

/**
 * Created by moskupols on 25.12.14.
 */
public interface CachingTableProvider extends TableProvider {
    void releaseTable(String name);
}
