package ru.fizteh.fivt.students.moskupols.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.util.List;

/**
 * Created by moskupols on 09.12.14.
 */
public interface KnownDiffStructuredTableProvider extends TableProvider {
    @Override
    KnownDiffStructuredTable getTable(String name);

    @Override
    KnownDiffStructuredTable createTable(String name, List<Class<?>> columnTypes) throws IOException;
}
