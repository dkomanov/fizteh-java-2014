package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.StoreableTable;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Welcome;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Show extends StoreableCommand {

    public Show() {
        super("show", 1);
    }

    private boolean checkForArgs(PrintStream err) {
        err.println("Bad show tables command.");
        if (batchModeInInteractive) {
            return false;
        }
        if (batchMode) {
            System.exit(-1);
        }
        return true;
    }

    @Override
    public boolean exec(Welcome dbConnector, String[] args, PrintStream out, PrintStream err) {

        if (args.length == 0) {
            return checkForArgs(err);
        }

        if (!args[0].equals("tables") || args.length > 1) {
            return checkForArgs(err);
        }

        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.readLock().lock();
        try {
            if (dbConnector.getTables().isEmpty() && dbConnector.getActiveTableProvider().getTables().isEmpty()) {
                return true;
            }

            for (Map.Entry<String, StoreableTable> a : dbConnector.getActiveTableProvider().getTables().entrySet()) {
                out.println(a.getKey() + " " + a.getValue().size());
            }
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }
}
