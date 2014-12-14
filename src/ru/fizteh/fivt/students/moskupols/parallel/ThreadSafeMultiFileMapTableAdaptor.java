package ru.fizteh.fivt.students.moskupols.parallel;

import ru.fizteh.fivt.students.moskupols.junit.KnownDiffTable;
import ru.fizteh.fivt.students.moskupols.multifilehashmap.MultiFileMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by moskupols on 14.12.14.
 */
public class ThreadSafeMultiFileMapTableAdaptor implements KnownDiffTable {
    private final MultiFileMap delegate;
    private ReadWriteLock delegateLock = new ReentrantReadWriteLock(true);

    private ThreadLocal<MapTransaction<String, String>> transaction;

    public ThreadSafeMultiFileMapTableAdaptor(MultiFileMap delegate) {
        this(delegate, MapTransactionImpl::new);
    }

    public ThreadSafeMultiFileMapTableAdaptor(
            MultiFileMap delegate, MapTransactionFactory<String, String> transactionFactory) {
        this.delegate = delegate;
        this.transaction = new ThreadLocal<MapTransaction<String, String>>() {
            @Override
            protected MapTransaction<String, String> initialValue() {
                return transactionFactory.create();
            }
        };
    }

    protected final String getFromDelegate(String key) {
        delegateLock.readLock().lock();
        try {
            return delegate.get(key);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            delegateLock.readLock().unlock();
        }
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public final String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key shouldn't be null");
        }
        if (transaction.get().getRemovedKeysProxy().contains(key)) {
            return null;
        }
        String ret = transaction.get().getPutProxy().get(key);
        if (ret != null) {
            return ret;
        } else {
            return getFromDelegate(key);
        }
    }

    @Override
    public final String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("key and value should both be not null");
        }
        String ret = transaction.get().getPutProxy().put(key, value);
        if (ret != null) {
            return ret;
        }
        if (transaction.get().getRemovedKeysProxy().remove(key)) {
            return null;
        }
        ret = getFromDelegate(key);
        if (ret == null) {
            transaction.get().getNewKeysProxy().add(key);
        }
        return ret;
    }

    @Override
    public final String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key shouldn't be null");
        }
        if (transaction.get().getRemovedKeysProxy().contains(key)) {
            return null;
        }
        String ret = transaction.get().getPutProxy().remove(key);
        if (ret != null) {
            if (!transaction.get().getNewKeysProxy().remove(key)) {
                transaction.get().getRemovedKeysProxy().add(key);
            }
        } else {
            ret = getFromDelegate(key);
            if (ret != null) {
                transaction.get().getRemovedKeysProxy().add(key);
            }
        }
        return ret;
    }

    @Override
    public final int size() {
        final int delegateSize;
        delegateLock.readLock().lock();
        try {
            delegateSize = delegate.size();
        } finally {
            delegateLock.readLock().unlock();
        }
        return delegateSize
                + transaction.get().getNewKeysProxy().size()
                - transaction.get().getRemovedKeysProxy().size();
    }

    @Override
    public final List<String> list() {
        List<String> ret;
        delegateLock.readLock().lock();
        try {
            ret = delegate.list().stream()
                    .filter(k -> !transaction.get().getRemovedKeysProxy().contains(k))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            delegateLock.readLock().unlock();
        }
        ret.addAll(transaction.get().getNewKeysProxy());
        return ret;
    }

    @Override
    public final int commit() {
        delegateLock.writeLock().lock();
        try {
            for (Map.Entry<String, String> entry : transaction.get().getPutProxy().entrySet()) {
                delegate.put(entry.getKey(), entry.getValue());
            }
            for (String k : transaction.get().getRemovedKeysProxy()) {
                delegate.remove(k);
            }
            delegate.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            delegateLock.writeLock().unlock();
        }
        return rollback();
    }

    @Override
    public final int rollback() {
        int ret = diff();
        transaction.get().getNewKeysProxy().clear();
        transaction.get().getPutProxy().clear();
        transaction.get().getRemovedKeysProxy().clear();
        return ret;
    }

    @Override
    public final int diff() {
        return transaction.get().getPutProxy().size() + transaction.get().getRemovedKeysProxy().size();
    }
}
