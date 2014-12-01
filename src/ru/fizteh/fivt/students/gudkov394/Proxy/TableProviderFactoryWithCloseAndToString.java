package ru.fizteh.fivt.students.gudkov394.Proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.gudkov394.Parallel.ParallelTableProvider;
import ru.fizteh.fivt.students.gudkov394.Parallel.ParallelTableProviderFactory;

import java.io.IOException;
import java.util.IllegalFormatCodePointException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by kagudkov on 30.11.14.
 */
public class TableProviderFactoryWithCloseAndToString extends ParallelTableProviderFactory implements TableProviderFactory, AutoCloseable{
    AtomicBoolean closed;

    public TableProviderFactoryWithCloseAndToString() {
        closed = new AtomicBoolean(false);
    }

    private void checkClosed() throws IllegalStateException{
        if(closed.get()) {
            throw new IllegalStateException();
        }
    }
    @Override
    public void close() throws Exception {
        closed.set(true);
    }

    @Override
    public TableProvider create(String path) {
        checkClosed();
        if(path == null) {
            throw new IllegalArgumentException();
        } else {
            return new TableProviderWithToStringAndClose(path);
        }
    }
}
