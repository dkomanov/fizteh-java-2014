package ru.fizteh.fivt.students.torunova.proxy.database;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

/**
 * Created by nastya on 24.11.14.
 */
public class DatabaseFactoryWrapper implements TableProviderFactory, AutoCloseable{
    private DatabaseFactory factory;
    private boolean closed;
    public DatabaseFactoryWrapper() {
        factory = new DatabaseFactory();
    }
    @Override
    public TableProvider create(String path) throws IOException {
        if (closed) {
            throw new IllegalStateException("Table provider factory already closed.");
        }
        return new DatabaseWrapper(factory.create(path));
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
