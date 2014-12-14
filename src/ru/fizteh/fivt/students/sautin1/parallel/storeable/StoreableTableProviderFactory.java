package ru.fizteh.fivt.students.sautin1.parallel.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by sautin1 on 12/11/14.
 */
public class StoreableTableProviderFactory implements TableProviderFactory {
    public StoreableTableProvider create(Path path, boolean autoCommit, StoreableTableIOToolsMultipleFiles ioTools)
            throws IOException {
        return new StoreableTableProvider(path, autoCommit, ioTools);
    }

    public StoreableTableProvider create(String path, boolean autoCommit, StoreableTableIOToolsMultipleFiles ioTools)
            throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("No directory provided");
        }
        Path rootDir = Paths.get(path);
        return create(rootDir, autoCommit, ioTools);
    }

    public StoreableTableProvider create(String path, boolean autoCommit)
            throws IOException {
        return create(path, autoCommit, new StoreableTableIOToolsMultipleFiles(16, 16, "UTF-8"));
    }

    @Override
    public StoreableTableProvider create(String path) throws IOException {
        return create(path, false);
    }
}
