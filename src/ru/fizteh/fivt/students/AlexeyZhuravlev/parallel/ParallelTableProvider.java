package ru.fizteh.fivt.students.AlexeyZhuravlev.parallel;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTableProvider;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTableProviderFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author AlexeyZhuravlev
 */
public class ParallelTableProvider implements TableProvider {

    StructuredTableProvider oldProvider;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    protected ParallelTableProvider(String path) throws IOException {
        StructuredTableProviderFactory factory = new StructuredTableProviderFactory();
        oldProvider = (StructuredTableProvider) factory.create(path);
    }

    @Override
    public Table getTable(String name) {
        lock.readLock().lock();
        try {
            StructuredTable origin = (StructuredTable) oldProvider.getTable(name);
            if (origin == null) {
                return null;
            } else {
                return new ParallelTable(origin, this);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        lock.writeLock().lock();
        try {
            StructuredTable origin = (StructuredTable) oldProvider.createTable(name, columnTypes);
            if (origin == null) {
                return null;
            } else {
                return new ParallelTable(origin, this);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        lock.writeLock().lock();
        try {
            oldProvider.removeTable(name);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Storeable createFor(Table table) {
        return oldProvider.createFor(((ParallelTable) table).getStructuredTable());
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return oldProvider.createFor(((ParallelTable) table).getStructuredTable(), values);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return oldProvider.deserialize(((ParallelTable) table).getStructuredTable(), value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return oldProvider.serialize(((ParallelTable) table).getStructuredTable(), value);
    }
}
