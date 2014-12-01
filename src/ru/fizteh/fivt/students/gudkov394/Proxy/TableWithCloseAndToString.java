package ru.fizteh.fivt.students.gudkov394.Proxy;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.gudkov394.Parallel.ParallelTable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by kagudkov on 30.11.14.
 */
public class TableWithCloseAndToString extends ParallelTable implements Table, AutoCloseable {
    AtomicBoolean closed;
    ParallelTable origin;

    public Table getTable() {
        return origin;
    }

    public boolean isClosed() {
        return closed.get();
    }

    private void checkIsClosed() {
        if (closed.get()) {
            throw new IllegalStateException();
        }
    }

    public TableWithCloseAndToString(Table originTmp, TableProvider providerTmp,
                                     ReentrantReadWriteLock lockTmp) {
        super(originTmp, lockTmp, (ru.fizteh.fivt.students.gudkov394.Parallel.ParallelTableProvider) providerTmp);
        closed = new AtomicBoolean(false);
    }

    @Override
    public void close() throws Exception {
        checkIsClosed();
        rollback();
        closed.set(true);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkIsClosed();
        Storeable tmp = super.put(key, value);
        if (tmp == null) {
            return null;
        } else {
            return new StoreableWithToString(tmp);
        }
    }

    @Override
    public Storeable remove(String key) {
        checkIsClosed();
        Storeable tmp = super.remove(key);
        if (tmp == null) {
            return null;
        } else {
            return new StoreableWithToString(tmp);
        }
    }

    @Override
    public int size() {
        checkIsClosed();
        return super.size();
    }

    @Override
    public List<String> list() {
        checkIsClosed();
        return super.list();
    }

    @Override
    public int commit() throws IOException {
        checkIsClosed();
        return super.commit();
    }

    @Override
    public int rollback() {
        checkIsClosed();
        return super.rollback();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        checkIsClosed();
        return super.getNumberOfUncommittedChanges();
    }

    @Override
    public int getColumnsCount() {
        checkIsClosed();
        return super.getColumnsCount();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkIsClosed();
        return super.getColumnType(columnIndex);
    }

    @Override
    public String getName() {
        checkIsClosed();
        return super.getName();
    }

    @Override
    public Storeable get(String key) {
        checkIsClosed();
        Storeable tmp = super.get(key);
        if (tmp == null) {
            return null;
        } else {
            return new StoreableWithToString(tmp);
        }
    }

    public String toString() {
        checkIsClosed();
        return this.getClass().getSimpleName() + "[" + super.getPath()  + "]";
    }
}
