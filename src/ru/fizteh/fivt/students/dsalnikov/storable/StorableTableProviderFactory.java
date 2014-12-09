package ru.fizteh.fivt.students.dsalnikov.storable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StorableTableProviderFactory implements TableProviderFactory, AutoCloseable, Serializable {
    private final Lock lock;
    WorkStatus status;
    private Map<String, StorableTableProvider> allProvidersMap;

    public StorableTableProviderFactory() {
        status = WorkStatus.WORKING;
        allProvidersMap = new HashMap<>();
        lock = new ReentrantLock(true);
    }

    @Override
    public TableProvider create(String directoryWithTables) throws IOException {
        status.canBeSafelyUsed();
        if (directoryWithTables == null || directoryWithTables.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory not set or set incorrectly");
        }
        File file = new File(directoryWithTables);
        try {
            file = file.getCanonicalFile();
        } catch (IOException exc) {
            throw new IllegalArgumentException("Invalid directory", exc);
        }
        String directory = file.getAbsolutePath();
        lock.lock();
        try {
            if (!allProvidersMap.containsKey(directory)) {
                allProvidersMap.put(directory, new StorableTableProvider(file));
            } else {
                if (!allProvidersMap.get(directory).isOkForOperations()) {
                    allProvidersMap.put(directory, new StorableTableProvider(file));
                }
            }
            return allProvidersMap.get(directory);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        status.canBeSafelyUsed();
        lock.lock();
        try {
            for (StorableTableProvider provider : allProvidersMap.values()) {
                provider.close();
            }
            status = WorkStatus.CLOSED;
        } finally {
            lock.unlock();
        }
    }
}
