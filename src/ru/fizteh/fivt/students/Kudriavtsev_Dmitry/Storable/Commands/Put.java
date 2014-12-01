package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Connector;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.CurrentStoreable;

import java.io.File;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class Put extends StoreableCommand {

    public Put() {
        super("put", 2);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        if (dbConnector.activeTable == null) {
            if (batchModeInInteractive) {
                System.err.println("No table");
                return false;
            }
            noTable();
            return true;
        }
        if (dbConnector.activeTable.signature.size() != args.length - 1) {
            System.err.println("Bad number of arguments");
            return !batchModeInInteractive;
        }
        for (int i = 1; i < args.length; ++i) {
            if (!dbConnector.activeTable.signature.contains(args[i].getClass())) {
                System.err.println("Signature not contains class " + args[i].getClass());
                return !batchModeInInteractive;
            }
        }
        Storeable temp = new CurrentStoreable(dbConnector.activeTable.signature);
        for (int i = 1; i < args.length; ++i) {
            temp.setColumnAt(i - 1, args[i]);
        }
        Storeable value = dbConnector.activeTable.put(args[0], temp);
        if (value != null) {
            System.out.println("overwrite");
            System.out.println(value);
        } else {
            System.out.println("new");
        }
        String newPath = dbConnector.activeTable.whereToSave("", args[0]).getKey();
        if (new File(newPath).exists() || dbConnector.activeTable.changedFiles.containsKey(newPath)) {
            Integer collisionCount = dbConnector.activeTable.changedFiles.get(newPath);
            if (collisionCount == null) {
                collisionCount = dbConnector.activeTable.countOfCollisionsInFile(new File(newPath).toPath());
            }
            ++collisionCount;
            dbConnector.activeTable.changedFiles.put(newPath, collisionCount);
        } else {
            dbConnector.activeTable.changedFiles.put(newPath, 0);
        }
        return true;
    }
}
