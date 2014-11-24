package ru.fizteh.fivt.students.AlexeyZhuravlev.proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.AlexeyZhuravlev.parallel.ParallelTableProviderFactory;

import java.io.IOException;

/**
 * @author AlexeyZhuravlev
 */
public class AdvancedTableProviderFactory extends ParallelTableProviderFactory {
    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException();
        } else {
            return new AdvancedTableProvider(path);
        }
    }
}
