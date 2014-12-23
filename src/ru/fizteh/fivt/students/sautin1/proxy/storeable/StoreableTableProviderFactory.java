package ru.fizteh.fivt.students.sautin1.proxy.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sautin1 on 12/11/14.
 */
public class StoreableTableProviderFactory implements TableProviderFactory, AutoCloseable {
    private boolean isClosed;
    private Set<StoreableTableProvider> providerSet;

    public StoreableTableProviderFactory() {
        isClosed = false;
        providerSet = new HashSet<>();
    }

    public StoreableTableProvider create(Path path, boolean autoCommit, StoreableTableIOToolsMultipleFiles ioTools)
            throws IOException {
        checkClosed();
        StoreableTableProvider provider = new StoreableTableProvider(path, autoCommit, ioTools);
        providerSet.add(provider);
        return provider;
    }

    public StoreableTableProvider create(String path, boolean autoCommit, StoreableTableIOToolsMultipleFiles ioTools)
            throws IOException {
        checkClosed();
        if (path == null) {
            throw new IllegalArgumentException("No directory provided");
        }
        Path rootDir = Paths.get(path);
        return create(rootDir, autoCommit, ioTools);
    }

    public StoreableTableProvider create(String path, boolean autoCommit)
            throws IOException {
        checkClosed();
        return create(path, autoCommit, new StoreableTableIOToolsMultipleFiles(16, 16, "UTF-8"));
    }

    @Override
    public StoreableTableProvider create(String path) throws IOException {
        checkClosed();
        return create(path, false);
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }
        for (StoreableTableProvider provider : providerSet) {
            provider.close();
        }
        isClosed = true;
    }

    private void checkClosed() {
        if (isClosed) {
            throw new IllegalStateException("table provider is closed");
        }
    }
}
