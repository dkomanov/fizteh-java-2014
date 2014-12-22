package ru.fizteh.fivt.students.pershik.Proxy;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by pershik on 11/27/14.
 */
public class CloseableTableProviderFactory implements TableProviderFactory, AutoCloseable {

    private boolean closed;

    public CloseableTableProviderFactory() { }

    @Override
    public CloseableTableProvider create(String dir)
            throws IllegalArgumentException, IOException {
        checkClosed();
        if (dir == null) {
            throw new IllegalArgumentException("null is not a directory");
        }
        File dbDir = new File(dir);
        if (dbDir.exists() && !dbDir.isDirectory()) {
            throw new IllegalArgumentException(dir + " isn't directory");
        }
        if (!dbDir.exists()) {
            if (!dbDir.mkdirs()) {
                throw new IOException("Can't create " + dir);
            }
        }
        return new CloseableTableProvider(dir);
    }

    @Override
    public void close() throws Exception {
        checkClosed();
        closed = true;
    }

    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Factory is closed");
        }
    }
}
