package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by Дмитрий on 25.11.2014.
 */

public class StoreableTableProviderFactory implements TableProviderFactory, AutoCloseable {

    private boolean closed;

    @Override
    public TableProvider create(String dir) throws IOException {
        if (dir == null || dir.isEmpty()) {
            throw new IllegalArgumentException("dir is null or empty");
        }
        File tempfile = new File(dir).getAbsoluteFile();
        if (!tempfile.mkdirs()) {
            throw new IllegalArgumentException("Can't create directory: " + dir);
        }
        if (tempfile.isFile()) {
            throw new IllegalArgumentException("db dir is a regular file");
        }
        if (!tempfile.canWrite()) {
            throw new IllegalArgumentException("dir is read only");
        }
            return new StoreableTableProvider(dir);
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
