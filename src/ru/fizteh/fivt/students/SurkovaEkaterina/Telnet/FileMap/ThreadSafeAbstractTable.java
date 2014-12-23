package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.FileMap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ThreadSafeAbstractTable implements Table, AutoCloseable {
    private final Lock transactionLock = new ReentrantLock(true);

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    protected final HashMap<String, Storeable> oldData;
    protected final ThreadLocal<TransactionModifications> transaction = new ThreadLocal<TransactionModifications>() {
        @Override
        public TransactionModifications initialValue() {
            return new TransactionModifications();
        }
    };

    private final String tableName;
    protected int size = 0;
    protected final String directory;
    protected TableState state;

    protected abstract void load() throws IOException;

    protected abstract void save() throws IOException;

    public ThreadSafeAbstractTable(final String dir, final String tName) {
        this.directory = dir;
        this.tableName = tName;
        oldData = new HashMap<String, Storeable>();
        try {
            load();
        } catch (IOException e) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Invalid file format!");
        }
        transaction.get().setTable(this);
        state = TableState.WORKING;
    }

    @Override
    public Storeable get(String key) {
        state.checkOperationCorrect();
        if (key == null || key.equals("")) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Key cannot be null!");
        }

        return transaction.get().getValue(key);
    }

    @Override
    public Storeable put(String key, Storeable value) {
        state.checkOperationCorrect();
        transaction.get().setTable(this);
        if (key == null || value == null) {
            String message = key == null ? ": Key " : ": Value ";
            throw new IllegalArgumentException(this.getClass().toString() + message + "cannot be null!");
        }

        Storeable oldValue = transaction.get().getValue(key);
        if (oldValue == null) {
            ++size;
        }

        transaction.get().addModification(key, value);
        return oldValue;
    }

    @Override
    public Storeable remove(String key) {
        state.checkOperationCorrect();
        if (key == null) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Key cannot be empty!");
        }
        if (get(key) == null) {
            return null;
        }

        Storeable oldValue = transaction.get().getValue(key);
        if (oldValue != null) {
            --size;
        }
        transaction.get().addModification(key, null);
        return oldValue;
    }

    @Override
    public List<String> list() {
        state.checkOperationCorrect();
        HashMap<String, Storeable> currentData = new HashMap<String, Storeable>(oldData);

        for (final String key : transaction.get().keySet()) {
            Storeable oldValue = oldData.get(key);
            Storeable newValue = transaction.get().getValue(key);
            if (oldValue != newValue) {
                if (newValue == null) {
                    currentData.remove(key);
                } else {
                    currentData.put(key, newValue);
                }
            }
        }

        List<String> list = new ArrayList<String>(currentData.keySet());

        return list;
    }

    public String getDirectory() {
        state.checkOperationCorrect();
        return directory;
    }

    @Override
    public int commit() {
        state.checkOperationCorrect();
        transactionLock.lock();
        try {
            int recordsCommitted = transaction.get().applyModifications();
            transaction.get().clear();

            try {
                save();
            } catch (IOException e) {
                System.out.println(this.getClass().toString() + e.getMessage());
                return 0;
            }

            return recordsCommitted;
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public int rollback() {
        state.checkOperationCorrect();
        int recordsDeleted = transaction.get().countModifications();
        transaction.get().clear();
        return recordsDeleted;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        state.checkOperationCorrect();
        return transaction.get().getUncommittedChanges();
    }

    @Override
    public String getName() {
        state.checkOperationCorrect();
        return tableName;
    }

    @Override
    public void close() {
        if (state.equals(TableState.CLOSED)) {
            return;
        }
        rollback();
        state = TableState.CLOSED;
    }

    public int size() {
        return size;
    }

    public boolean isClosed() {
        return state.equals(TableState.CLOSED);
    }

    protected abstract DatabaseFileDescriptor makeDescriptor(String key);
}
