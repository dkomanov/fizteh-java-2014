package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

public class MyStoreableTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException();
        } else {
            return new MyStoreableTableProvider(path);
        }
    }
}
