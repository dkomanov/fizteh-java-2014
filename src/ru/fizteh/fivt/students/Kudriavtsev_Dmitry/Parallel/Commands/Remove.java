package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Connector;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class Remove extends StoreableCommand {
    public Remove() {
        super("remove", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        if (dbConnector.getActiveTable() == null) {
            if (batchModeInInteractive) {
                System.err.println("No table");
                return false;
            }
            noTable();
            return true;
        }
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.readLock().lock();
        try {
            if (dbConnector.getActiveTable().remove(args[0]) != null) {
                System.out.println("removed");
                dbConnector.getActiveTable().getChangedFiles().put(
                        dbConnector.getActiveTable().whereToSave("", args[0]).getKey(), 0);
            } else {
                System.err.println("not found");
                if (batchModeInInteractive) {
                    return false;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }

}
