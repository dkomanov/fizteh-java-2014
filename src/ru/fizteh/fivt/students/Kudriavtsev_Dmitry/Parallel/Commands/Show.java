package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Connector;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.StoreableTable;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Show extends StoreableCommand {

    public Show() {
        super("show", 1);
    }

    private boolean checkForArgs() {
        System.err.println("Bad show tables command.");
        if (batchModeInInteractive) {
            return false;
        }
        if (batchMode) {
            System.exit(-1);
        }
        return true;
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {

        if (args.length == 0) {
            return checkForArgs();
        }

        if (!args[0].equals("tables") || args.length > 1) {
            return checkForArgs();
        }

        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.readLock().lock();
        try {
            if (dbConnector.getTables().isEmpty() && dbConnector.getActiveTableProvider().getTables().isEmpty()) {
                return true;
            }

            for (Map.Entry<String, StoreableTable> a : dbConnector.getActiveTableProvider().getTables().entrySet()) {
                System.out.println(a.getKey() + " " + a.getValue().size());
            }
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }
}
