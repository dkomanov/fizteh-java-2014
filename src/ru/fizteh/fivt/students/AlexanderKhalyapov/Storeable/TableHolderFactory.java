package ru.fizteh.fivt.students.AlexanderKhalyapov.Storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

public class TableHolderFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        return new TableHolder(path);
    }
}
