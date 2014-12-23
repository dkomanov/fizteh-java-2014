package ru.fizteh.fivt.students.moskupols.junit;

import ru.fizteh.fivt.students.moskupols.multifilehashmap.MultiFileMap;
import ru.fizteh.fivt.students.moskupols.multifilehashmap.MultiFileMapProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by moskupols on 17.11.14.
 */
public class MultiFileMapTableProvider implements KnownDiffTableProvider {
    private final MultiFileMapProvider delegatedProvider;
    private final MultiFileMapTableAdaptorFactory adaptorFactory;

    public MultiFileMapTableProvider(Path dbPath) {
        this(dbPath, MultiFileMapTableAdaptor::new);
    }

    public MultiFileMapTableProvider(Path dbPath, MultiFileMapTableAdaptorFactory adaptorFactory) {
        this.adaptorFactory = adaptorFactory;
        try {
            delegatedProvider = new MultiFileMapProvider(dbPath);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void checkNotNull(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name should not be null");
        }
    }

    @Override
    public KnownDiffTable getTable(String name) {
        checkNotNull(name);
        MultiFileMap t;
        try {
            t = delegatedProvider.getTable(name);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return t == null ? null : adaptorFactory.adapt(t);
    }

    @Override
    public KnownDiffTable createTable(String name) {
        checkNotNull(name);
        MultiFileMap t;
        try {
            t = delegatedProvider.createTable(name);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return t == null ? null : adaptorFactory.adapt(t);
    }

    @Override
    public void removeTable(String name) {
        checkNotNull(name);
        try {
            delegatedProvider.removeTable(name);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<String> listNames() {
        return delegatedProvider.listNames();
    }
}
