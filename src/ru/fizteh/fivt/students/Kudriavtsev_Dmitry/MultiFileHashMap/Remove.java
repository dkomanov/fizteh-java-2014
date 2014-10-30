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
            if (batchModeInInteractive) {
                return false;
            }
            return true;
        }
        if (dbConnector.activeTable == null) {
            if (batchModeInInteractive) {
                System.err.println("No table");
                return false;
            }
            noTable();
            return true;
        }
        if (dbConnector.activeTable.remove(args[0]) != null) {
            System.out.println("removed");
            dbConnector.activeTable.changedFiles.put(dbConnector.activeTable.whereToSave(args[0]).getKey(), 0);
        } else {
            System.err.println("not found");
            if (batchModeInInteractive) {
                return false;
            }
        }
        return true;
    }

}
