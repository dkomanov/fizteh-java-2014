package ru.fizteh.fivt.students.moskupols.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;

/**
 * Created by moskupols on 09.12.14.
 */
public interface KnownDiffTableProvider extends TableProvider {
    @Override
    KnownDiffTable getTable(String name);

    @Override
    KnownDiffTable createTable(String name);
}
