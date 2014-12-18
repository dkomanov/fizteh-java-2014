package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Connector;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.CurrentStoreable;

import java.io.File;
import java.text.ParseException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class Put extends StoreableCommand {

    public Put() {
        super("put", 2);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (args == null || args.length <= 1) {
            System.err.println("Incorrect number of arguments in " + name);
            if (batchMode) {
                System.exit(-1);
            }
            return !batchModeInInteractive;
        }
        if (args.length > 2) {
            for (int i = 2; i < args.length; ++i) {
                args[1] = args[1].concat(" " + args[i]);
            }
        }
        if (dbConnector.getActiveTable() == null) {
            if (batchModeInInteractive) {
                System.err.println("No table");
                return false;
            }
            noTable();
            return true;
        }
        Storeable value;
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.readLock().lock();
        try {
            try {
                value = dbConnector.getActiveTable().put(args[0],
                        dbConnector.getActiveTableProvider().deserialize(dbConnector.getActiveTable(), args[1]));
            } catch (ParseException e) {
                System.err.println("Parse Exception in deserialize");
                return !batchModeInInteractive;
            }
            if (value != null) {
                System.out.println("overwrite");
                for (Object val : ((CurrentStoreable) value).getValues()) {
                    System.out.println(val.toString());
                }
            } else {
                System.out.println("new");
            }
            String newPath = dbConnector.getActiveTable().whereToSave("", args[0]).getKey();
            if (new File(newPath).exists() || dbConnector.getActiveTable().getChangedFiles().containsKey(newPath)) {
                Integer collisionCount = dbConnector.getActiveTable().getChangedFiles().get(newPath);
                if (collisionCount == null) {
                    collisionCount = dbConnector.getActiveTable().countOfCollisionsInFile(new File(newPath).toPath());
                }
                ++collisionCount;
                dbConnector.getActiveTable().getChangedFiles().put(newPath, collisionCount);
            } else {
                dbConnector.getActiveTable().getChangedFiles().put(newPath, 0);
            }
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }
}
