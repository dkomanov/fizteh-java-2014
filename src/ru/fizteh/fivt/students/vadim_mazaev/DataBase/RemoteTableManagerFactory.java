package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

public final class RemoteTableManagerFactory implements RemoteTableProviderFactory, AutoCloseable {
    private Set<TableManager> createdManagers = new HashSet<>();
    private boolean invalid;

    public RemoteTableManagerFactory() {
        // Do nothing, only for implemented interface.
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
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        // TODO Auto-generated method stub
        return null;
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
