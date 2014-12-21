package ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase;


import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

/**
 * Created by user1 on 21.10.2014.
 */
public class DataBaseTableProviderFactory implements TableProviderFactory, AutoCloseable {
    boolean closed = false;

    void isClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("factory is closed");
        }
    }

    @Override
    public DataBaseTableProvider create(String path) throws IOException {
        isClosed();
        if (path == null) {
            throw new IllegalArgumentException("Null table path");
        }
        try {
            return new DataBaseTableProvider(path);
        } catch (Exception e) {
            return null;
        }
    }

    public DataBaseTableProvider create(String dir, boolean testMode) throws Exception {
        isClosed();
        if (dir == null) {
            throw new IllegalArgumentException("Null table path");
        }
        try {
            return new DataBaseTableProvider(dir, testMode);
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
