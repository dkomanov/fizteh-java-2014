package ru.fizteh.fivt.students.moskupols.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.moskupols.multifilehashmap.MultiFileMap;
import ru.fizteh.fivt.students.moskupols.multifilehashmap.MultiFileMapProvider;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by moskupols on 17.11.14.
 */
public class MultiFileMapTableProvider implements TableProvider {
    private final MultiFileMapProvider delegatedProvider;

    public MultiFileMapTableProvider(Path dbPath) {
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
        return t == null ? null : new MultiFileMapTableAdaptor(t);
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
        return t == null ? null : new MultiFileMapTableAdaptor(t);
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
}
