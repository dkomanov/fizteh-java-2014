package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 16.11.14.
 */

public class StoreableDataTableProviderFactory implements TableProviderFactory, AutoCloseable {
    private boolean closed = false;

    @Override
    public TableProvider create(String path) throws IOException {
        if (closed) {
            throw new IllegalStateException("create failed: TableProviderFactory have been closed");
        }
        if (path == null) {
            throw new IllegalArgumentException("Directory is null");
        }
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Empty directory name");
        }
        return new StoreableDataTableProvider(path);
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
        }
    }
}
