package ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableHolderFactory implements TableProviderFactory, AutoCloseable {
    private boolean valid = true;
    private List<TableHolder> createdTableHolders;

    public TableHolderFactory() {
        createdTableHolders = new ArrayList<>();
    }

    @Override
    public TableProvider create(String path) throws IOException {
        if (!valid) {
            throw new IllegalStateException("Factory was closed\n");
        }
        Utility.checkIfObjectsNotNull(path);
        TableHolder newTableHolder = new TableHolder(path);
        createdTableHolders.add(newTableHolder);
        return newTableHolder;
    }

    @Override
    public void close() throws Exception {
        createdTableHolders.forEach(TableHolder::close);
        valid = false;
    }

}
