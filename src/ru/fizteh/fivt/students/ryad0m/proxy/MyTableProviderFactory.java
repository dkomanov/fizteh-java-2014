package ru.fizteh.fivt.students.ryad0m.proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class MyTableProviderFactory implements TableProviderFactory, AutoCloseable {

    private boolean closed = false;

    @Override
    public TableProvider create(String path) throws IOException {
        if (closed) {
            throw new IllegalStateException();
        } else if (path == null) {
            throw new IllegalArgumentException();
        }
        return new MyTableProvider(Paths.get(path).toAbsolutePath().normalize());
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
