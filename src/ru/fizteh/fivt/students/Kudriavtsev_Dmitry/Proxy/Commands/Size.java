package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Welcome;

import java.io.PrintStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Дмитрий on 31.10.2014.
 */
public class Size extends StoreableCommand {
    public Size() {
        super("size", 0);
    }

    @Override
    public boolean exec(Welcome dbConnector, String[] args, PrintStream out, PrintStream err) {
        if (!checkArguments(args.length, err)) {
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
        out.println(size);
        return true;
    }
}
