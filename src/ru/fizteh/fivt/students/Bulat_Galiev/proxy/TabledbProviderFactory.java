package ru.fizteh.fivt.students.Bulat_Galiev.proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class TabledbProviderFactory implements TableProviderFactory {
    boolean closed = false;

    void isClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("factory is closed");
        }
    }

    @Override
    public final TableProvider create(final String dir) {
        if (dir != null) {
            return new TabledbProvider(dir);
        } else {
            throw new IllegalArgumentException("Null name directory");
        }
    }

    public void close() throws Exception {
        closed = true;
    }
}
