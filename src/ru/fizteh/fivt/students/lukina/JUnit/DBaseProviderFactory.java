package ru.fizteh.fivt.students.lukina.JUnit;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class DBaseProviderFactory implements TableProviderFactory, AutoCloseable {
    private HashSet<DBaseProvider> providers = new HashSet<>();
    private volatile boolean isClosed = false;

    @Override
    public TableProvider create(String path) throws IOException {
        if (isClosed) {
            throw new IllegalStateException("TableProviderFactory is closed");
        }
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("dir not selected");
        }
        File root = new File(path);
        if (!root.exists()) {
            if (!root.mkdirs()) {
                throw new IOException("Directory cannot be created");
            }
        }
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }
        DBaseProvider dataBase = null;
        dataBase = new DBaseProvider(path);
        providers.add(dataBase);
        return dataBase;
    }

    @Override
    public void close() throws Exception {
        for (DBaseProvider prov : providers) {
            prov.close();
        }
        isClosed = true;
    }
}
