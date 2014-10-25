package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;


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
            return false;
        }
        if (dbConnector.activeTable == null) {
            noTable();
            return false;
        }
        String value = dbConnector.activeTable.put(args[0], args[1]);
        if (value != null) {
            System.out.println("overwrite");
            System.out.println(value);
        } else {
            System.out.println("new");
        }
        String newPath = dbConnector.activeTable.whereToSave(args[0]).getKey();
        if (dbConnector.activeTable.changedFiles.containsKey(newPath)) {
            Integer collisionCount = dbConnector.activeTable.changedFiles.get(newPath);
            ++collisionCount;
            dbConnector.activeTable.changedFiles.put(newPath, collisionCount);
        } else {
            dbConnector.activeTable.changedFiles.put(newPath, 0);
        }
        return true;
    }
}
