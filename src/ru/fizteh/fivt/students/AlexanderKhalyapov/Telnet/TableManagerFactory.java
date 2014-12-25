package ru.fizteh.fivt.students.AlexanderKhalyapov.Telnet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public final class TableManagerFactory implements TableProviderFactory, AutoCloseable {
    private Set<TableProvider> createdManagers = new HashSet<>();
    private boolean invalid;

    public TableManagerFactory() {
        // Do nothing, only for implemented imterface.
    }

    @Override
    public TableProvider create(String dir) throws IOException {
        checkFactoryIsNotInvalid();
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        TableProvider manager = new TableProvider(dir);
        createdManagers.add(manager);
        return manager;
    }

    @Override
    public void close() {
        checkFactoryIsNotInvalid();
        for (TableProvider manager : createdManagers) {
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
