package ru.fizteh.fivt.students.andreyzakharov.remotefilemap;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class MultiFileTableProviderFactory implements AutoCloseable, RemoteTableProviderFactory {
    boolean closed;

    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        checkClosed();
        try {
            MultiFileTableProvider provider = new MultiFileTableProvider(Paths.get(""));
            provider.connect(hostname, port);
            return provider;
        } catch (ConnectionInterruptException e) {
            throw new IOException(e.getMessage());
        }
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
