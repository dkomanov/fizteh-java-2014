package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;


/**
 * Created by Дмитрий on 04.10.14.
 */
public class Remove extends Command {
    public Remove() {
        super("remove", 1);
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
        if (dbConnector.activeTable.remove(args[0]) != null) {
            System.out.println("removed");
            dbConnector.activeTable.changedFiles.put(dbConnector.activeTable.whereToSave(args[0]).getKey(), 0);
        } else {
            System.out.println("not found");
        }
        return true;
    }

}
