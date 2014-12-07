package ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_structure;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class TableManagerFactory implements TableProviderFactory, AutoCloseable {
    private List<TableManager> providersSet = new ArrayList<>();
    private AtomicBoolean closed = new AtomicBoolean(false);

    public TableManagerFactory() {
        // Does nothing, for implementation only.
    }

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        TableManager newManager = new TableManager(dir);
        providersSet.add(newManager);
        return newManager;
    }

    @Override
    public void close() throws Exception {
        if (!closed.getAndSet(true)) {
            for (TableManager currentManager : providersSet) {
                currentManager.close();
            }
        } else {
            throw new IllegalStateException("TableProviderFactory has already been closed");
        }
    }
}
