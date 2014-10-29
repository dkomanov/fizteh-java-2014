package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;


import java.io.File;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class Put extends Command {

    public Put() {
        super("put", 2);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            if (packageModeInInteractive) {
                return false;
            }
            return true;
        }
        if (dbConnector.activeTable == null) {
            if (packageModeInInteractive) {
                return false;
            }
            noTable();
            return true;
        }
        String value = dbConnector.activeTable.put(args[0], args[1]);
        if (value != null) {
            System.out.println("overwrite");
            System.out.println(value);
        } else {
            System.out.println("new");
        }
        String newPath = dbConnector.activeTable.whereToSave(args[0]).getKey();
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
