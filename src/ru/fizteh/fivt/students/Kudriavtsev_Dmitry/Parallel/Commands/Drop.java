package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.StoreableTable;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Welcome;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Drop extends StoreableCommand {

    public Drop() {
        super("drop", 1);
    }

    @Override
    public  boolean exec(Welcome dbConnector, String[] args, PrintStream out, PrintStream err) {
        if (!checkArguments(args.length, err)) {
            return !batchModeInInteractive;
        }

        boolean inProvider = true;
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.readLock().lock();
        try {
            StoreableTable map = dbConnector.getActiveTableProvider().getTables().get(args[0]);
            if (map == null) {
                map = dbConnector.getTables().get(args[0]);
                inProvider = false;
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
            if (dbConnector.getActiveTable() == map) {
                if (inProvider) {
                    dbConnector.getActiveTableProvider().getTables().remove(args[0]);
                } else {
                    dbConnector.getTables().remove(args[0]);
                }
                dbConnector.setActiveTable(null);
            }
            try {
                if (dbConnector != null) {
                    if (inProvider) {
                        dbConnector.getActiveTableProvider().getTables().remove(args[0]);
                    } else {
                        dbConnector.getTables().remove(args[0]);
                    }
                }
                if (dbConnector.getActiveTable() == null) {
                    map.deleteFiles("", true);
                    Files.delete(new File(map.dbPath + File.separator + "signature.tsv").toPath());
                } else {
                    map.deleteFiles(dbConnector.getActiveTable().getName(), true);
                }
                Files.delete(map.dbPath);
            } catch (Exception e) {
                err.println("Exception in drop: can't delete " + map.dbPath.toString()
                        + " because " + e.getLocalizedMessage());
                System.exit(-1);
            }
        } finally {
            lock.readLock().unlock();
        }
        out.println("dropped");
        return true;
    }
}
