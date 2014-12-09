package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

import ru.fizteh.fivt.storage.structured.*;

import java.io.IOException;

public class ParallelTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException();
        }
        return new ParallelTableProvider(path);
    }
}
