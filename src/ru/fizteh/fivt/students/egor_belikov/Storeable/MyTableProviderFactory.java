package ru.fizteh.fivt.students.egor_belikov.Storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

public class MyTableProviderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("MyTableProviderFactory: Directory name is null");
        }
        return new MyTableProvider(path);
    }
}