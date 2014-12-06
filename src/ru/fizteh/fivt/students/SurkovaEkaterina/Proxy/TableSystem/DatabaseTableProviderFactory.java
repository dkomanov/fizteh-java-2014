package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.FileMap.TableState;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTableProviderFactory implements TableProviderFactory, AutoCloseable {
    private TableState state = TableState.WORKING;
    private List<ThreadSafeDatabaseTableProvider> providers = new ArrayList<ThreadSafeDatabaseTableProvider>();

    @Override
    public TableProvider create(String directory) throws IOException {
        state.checkOperationCorrect();
        if (directory == null) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Directory cannot be null!");
        }

        if (directory.trim().isEmpty()) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Directory's name cannot be empty!");
        }

        File databaseDirectory = new File(directory);
        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException(this.getClass().toString()
                    + ": Cannot create database in file. Provide a directory, please");
        }

        if (!databaseDirectory.exists()) {
            if (!databaseDirectory.mkdir()) {
                throw new IOException(this.getClass().toString() + ": Provider is unavailable!");
            }
        }
        ThreadSafeDatabaseTableProvider newProvider
                = new ThreadSafeDatabaseTableProvider(databaseDirectory.getAbsolutePath());
        providers.add(newProvider);
        return newProvider;
    }

    @Override
    public void close() throws Exception {
        for (ThreadSafeDatabaseTableProvider provider : providers) {
            provider.close();
        }
        state = TableState.CLOSED;
    }
}
