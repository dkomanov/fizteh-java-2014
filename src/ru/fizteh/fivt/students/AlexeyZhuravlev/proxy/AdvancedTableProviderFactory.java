package ru.fizteh.fivt.students.AlexeyZhuravlev.proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.parallel.ParallelTableProviderFactory;

import java.io.IOException;

/**
 * @author AlexeyZhuravlev
 */
public class AdvancedTableProviderFactory extends ParallelTableProviderFactory implements TableProviderFactory,
                                                                               AutoCloseable {

    private boolean closed;

    public AdvancedTableProviderFactory() {
        closed = false;
    }

    @Override
    public TableProvider create(String path) throws IOException {
        if (closed) {
            throw new IllegalStateException();
        } else if (path == null) {
            throw new IllegalArgumentException();
        } else {
            return new AdvancedTableProvider(path);
        }
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
