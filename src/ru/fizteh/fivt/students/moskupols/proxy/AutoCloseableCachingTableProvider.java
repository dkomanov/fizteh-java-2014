package ru.fizteh.fivt.students.moskupols.proxy;

import ru.fizteh.fivt.students.moskupols.parallel.CachingTableProvider;

import java.io.IOException;
import java.util.List;

/**
 * Created by moskupols on 25.12.14.
 */
public interface AutoCloseableCachingTableProvider extends AutoCloseable, CachingTableProvider {
    @Override
    AutoCloseableTable getTable(String name);

    @Override
    AutoCloseableTable createTable(String name, List<Class<?>> columnTypes) throws IOException;
}
