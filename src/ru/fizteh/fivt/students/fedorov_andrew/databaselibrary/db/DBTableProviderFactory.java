package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Log;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.LoggingProxyFactoryImpl;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.ValidityController;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.ValidityController.KillLock;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.ValidityController.UseLock;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.IdentityHashMap;

public final class DBTableProviderFactory implements TableProviderFactory, AutoCloseable {
    private static final LoggingProxyFactory LOGGING_PROXY_FACTORY = new LoggingProxyFactoryImpl();
    private static final Writer LOG_WRITER;

    static {
        Writer tempWriter;

        try {
            tempWriter = new OutputStreamWriter(new FileOutputStream("Proxy.log"));
        } catch (IOException exc) {
            Log.log(DBTableProviderFactory.class, exc, "Failed to create log");
            tempWriter = null;
        }

        LOG_WRITER = tempWriter;
    }

    private final ValidityController validityController = new ValidityController();
    private final IdentityHashMap<AutoCloseableProvider, Boolean> generatedProviders =
            new IdentityHashMap<>();

    static <T> T wrapImplementation(T implementation, Class<T> interfaceClass) {
        if (LOG_WRITER != null) {
            return (T) LOGGING_PROXY_FACTORY.wrap(LOG_WRITER, implementation, interfaceClass);
        } else {
            return implementation;
        }
    }

    @Override
    public synchronized void close() {
        try (KillLock lock = validityController.useAndKill()) {
            for (AutoCloseableProvider provider : generatedProviders.keySet()) {
                provider.close();
            }
            generatedProviders.clear();
        }
    }

    /**
     * Unregisters the given provider.
     * @param provider
     *         Pure (not proxied) link to the closed provider.
     */
    synchronized void onProviderClosed(AutoCloseableProvider provider) {
        try (UseLock useLock = validityController.use()) {
            generatedProviders.remove(provider);
        }
    }

    private void checkDatabaseDirectory(final Path databaseRoot) throws DatabaseIOException {
        if (!Files.isDirectory(databaseRoot)) {
            throw new DatabaseIOException("Database root must be a directory");
        }

        try (DirectoryStream<Path> tableDirs = Files.newDirectoryStream(databaseRoot)) {
            for (Path tableDirectory : tableDirs) {
                if (!Files.isDirectory(tableDirectory)) {
                    throw new DatabaseIOException(
                            "Non-directory path found in database folder: " + tableDirectory.getFileName());
                }
            }
        } catch (IOException exc) {
            throw new DatabaseIOException(
                    "Failed to scan database directory: " + exc.getMessage(), exc);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    @Override
    public synchronized AutoCloseableProvider create(String dir)
            throws IllegalArgumentException, DatabaseIOException {
        try (UseLock useLock = validityController.use()) {
            Utility.checkNotNull(dir, "Directory");

            Path databaseRoot = Paths.get(dir).normalize();
            if (!Files.exists(databaseRoot)) {
                if (databaseRoot.getParent() == null || !Files.isDirectory(databaseRoot.getParent())) {
                    throw new DatabaseIOException(
                            "Database directory parent path does not exist or is not a directory");
                }

                try {
                    Files.createDirectory(databaseRoot);
                } catch (IOException exc) {
                    throw new DatabaseIOException("Failed to establish database on path " + dir, exc);
                }
            } else {
                checkDatabaseDirectory(databaseRoot);
            }

            AutoCloseableProvider provider = new DBTableProvider(databaseRoot, this);
            AutoCloseableProvider wrappedProvider = wrapImplementation(provider, AutoCloseableProvider.class);
            generatedProviders.put(provider, Boolean.TRUE);
            return wrappedProvider;
        }
    }
}
