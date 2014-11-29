package ru.fizteh.fivt.students.standy66_new.storage.structured;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by andrew on 07.11.14.
 */
public class StructuredDatabaseFactory implements TableProviderFactory, AutoCloseable {
    private boolean closed = false;

    @Override
    public StructuredDatabase create(String path) throws IOException {
        if (closed) {
            throw new IllegalStateException("StructuredDatabaseFactory was closed, but then method closed");
        }
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("dir is null or empty");
        }
        File databaseFile = new File(path).getAbsoluteFile();
        return new StructuredDatabase(databaseFile);
    }

    public void close() {
        if (!closed) {
            closed = true;
        }
    }
}
