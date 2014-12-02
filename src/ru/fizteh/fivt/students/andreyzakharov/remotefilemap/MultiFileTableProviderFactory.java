package ru.fizteh.fivt.students.andreyzakharov.remotefilemap;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.nio.file.Paths;

public class MultiFileTableProviderFactory implements AutoCloseable, TableProviderFactory {
    boolean closed;

    @Override
    public TableProvider create(String dir) {
        checkClosed();
        try {
            return new MultiFileTableProvider(Paths.get("").resolve(Paths.get(dir)));
        } catch (ConnectionInterruptException e) {
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        if (!closed) {
            closed = true;
        }
    }

    private void checkClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("factory: factory was closed");
        }
    }
}
