package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Convenience class to control one's validity.
 */
public final class ValidityController {
    private final ReadWriteLock validityLock = new ReentrantReadWriteLock(true);
    private boolean valid = true;

    private void checkValid() {
        if (!valid) {
            throw new IllegalStateException("This object has been invalidated: ");
        }
    }

    /**
     * Invalidates
     */
    private void invalidate() {
        validityLock.writeLock().lock();
        try {
            checkValid();
            valid = false;
        } finally {
            validityLock.writeLock().unlock();
        }
    }

    /**
     * Returns activated lock on the use of the object. While object is used, nobody can invalidate it
     * (except
     * the host thread of this lock).
     * @throws java.lang.IllegalStateException
     *         if this object has been already invalidated.
     */
    public UseLock use() throws IllegalStateException {
        validityLock.readLock().lock();
        try {
            checkValid();
            validityLock.readLock().lock();
            return new UseLock();
        } finally {
            validityLock.readLock().unlock();
        }
    }

    /**
     * Returns activated unique lock for the use of the object. After use object is invalidated.
     * @throws java.lang.IllegalStateException
     *         if this object has been already invalidated.
     */
    public KillLock useAndKill() throws IllegalStateException {
        validityLock.writeLock().lock();
        try {
            checkValid();
            validityLock.writeLock().lock();
            return new KillLock();
        } finally {
            validityLock.writeLock().unlock();
        }
    }

    public class UseLock implements AutoCloseable {
        @Override
        public void close() {
            validityLock.readLock().unlock();
        }
    }

    public class KillLock implements AutoCloseable {
        @Override
        public void close() {
            invalidate();
            validityLock.writeLock().unlock();
        }
    }

}
