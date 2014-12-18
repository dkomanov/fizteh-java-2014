package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Connector;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Дмитрий on 31.10.2014.
 */
public class Size extends StoreableCommand {
    public Size() {
        super("size", 0);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        int size = 0;
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.readLock().lock();
        try {
            for (String s : dbConnector.getTables().keySet()) {
                size += dbConnector.getTables().get(s).size();
            }
            for (String s : dbConnector.getActiveTableProvider().getTables().keySet()) {
                size += dbConnector.getActiveTableProvider().getTables().get(s).size();
            }
        } finally {
            lock.readLock().unlock();
        }
        System.out.println(size);
        return true;
    }
}
