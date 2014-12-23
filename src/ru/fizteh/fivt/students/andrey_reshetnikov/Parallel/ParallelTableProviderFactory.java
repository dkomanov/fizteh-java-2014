package ru.fizteh.fivt.students.andrey_reshetnikov.Parallel;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;

public class ParallelTableProviderFactory implements TableProviderFactory{
    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException();
        } else {
            return new ParallelTableProvider(path);
        }
    }
}
