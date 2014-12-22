package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public final class TableManagerFactory implements TableProviderFactory, AutoCloseable {
    private Set<TableManager> createdManagers = new HashSet<>();
    private boolean invalid;

    public TableManagerFactory() {
        // Do nothing, only for implemented imterface.
    }

    @Override
    public TableManager create(String dir) throws IOException {
        checkFactoryIsNotInvalid();
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        TableManager manager = new TableManager(dir);
        createdManagers.add(manager);
        return manager;
    }

    @Override
    public void close() {
        checkFactoryIsNotInvalid();
        for (TableManager manager : createdManagers) {
            manager.close();
        }
        invalid = true;
    }

    private void checkFactoryIsNotInvalid() {
        if (invalid) {
            throw new IllegalStateException("Factory is invalid");
        }
    }
}
