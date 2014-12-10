package ru.fizteh.fivt.students.andreyzakharov.remotefilemap;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class MultiFileTableProviderFactory implements AutoCloseable, RemoteTableProviderFactory {
    boolean closed;

    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        checkClosed();
        return null;
    }

    public RemoteTableProvider create(String dir) {
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
