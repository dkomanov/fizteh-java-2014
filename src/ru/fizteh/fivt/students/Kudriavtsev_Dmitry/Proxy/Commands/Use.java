package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.StoreableTable;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Welcome;

import java.io.PrintStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Use extends StoreableCommand {

    public Use() {
        super("use", 1);
    }

    @Override
    public boolean exec(Welcome dbConnector, String[] args, PrintStream out, PrintStream err) {
        if (!checkArguments(args.length, err)) {
            return !batchModeInInteractive;
        }
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.readLock().lock();
        try {
            if (dbConnector.getActiveTable() != null) {
                if (!dbConnector.getActiveTable().getRemoved().isEmpty()
                        || !dbConnector.getActiveTable().getNewKey().isEmpty()) {
                    int count = 0;
                    for (String s : dbConnector.getActiveTable().getRemoved().keySet()) {
                        if (dbConnector.getActiveTable().getActiveTable().containsKey(s)) {
                            ++count;
                        }
                    }
                    count += dbConnector.getActiveTable().getNewKey().size();
                    out.println(count + " unsaved changes");
                    return true;
                }
            }
            StoreableTable map = dbConnector.getActiveTableProvider().getTables().get(args[0]);
            if (map == null) {
                map = dbConnector.getTables().get(args[0]);
            }
            if (map == null) {
                err.println(args[0] + " not exists");
                if (batchModeInInteractive) {
                    return false;
                }
                if (batchMode) {
                    System.exit(-1);
                }
                return true;
            }
            if (dbConnector.getActiveTable() != null) {
                dbConnector.getActiveTable().unload(
                    dbConnector.getActiveTable(), dbConnector.getActiveTable().getName());
                if (dbConnector.getActiveTable().dbPath.getFileName().toString().equals(args[0])) {
                    out.println("using " + args[0]);
                    return true;
                }
            }
            dbConnector.setActiveTable(map);
        } finally {
            lock.readLock().unlock();
        }
        out.println("using " + args[0]);
        return true;
    }

}
