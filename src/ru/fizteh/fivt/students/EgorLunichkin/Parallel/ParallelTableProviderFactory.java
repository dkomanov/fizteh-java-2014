package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

import ru.fizteh.fivt.storage.structured.*;

public class ParallelTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String path) {
        if (path == null) {
            throw new IllegalArgumentException();
        }
        return new ParallelTableProvider(path);
    }
}
