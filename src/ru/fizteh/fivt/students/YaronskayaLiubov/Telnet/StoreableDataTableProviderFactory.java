package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;


/**
 * Created by luba_yaronskaya on 16.11.14.
 */

public class StoreableDataTableProviderFactory implements RemoteTableProviderFactory, AutoCloseable {
    private boolean closed = false;

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

    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        if (closed) {
            throw new IllegalStateException("create failed: TableProviderFactory have been closed");
        }
       String path = System.getProperty("user.dir");
        if (path == null) {
            throw new IllegalArgumentException("Directory is null");
        }
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Empty directory name");
        }
        StoreableDataTableProvider provider = (StoreableDataTableProvider) create(path);
        RemoteDataTableProvider remoteProvider = new RemoteDataTableProvider(provider);
        remoteProvider.connect(hostname, port);
        return remoteProvider;
    }
}
