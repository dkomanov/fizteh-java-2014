package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Use extends Command {

    public Use() {
        super("use", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }

        MFHMap map = dbConnector.tables.get(args[0]);
        if (map == null) {
            System.err.println(args[0] + " not exists");
            if (batchModeInInteractive) {
                return false;
            }
            if (batchMode) {
                System.exit(-1);
            }
            return true;
        }
        if (dbConnector.activeTable != null) {
            dbConnector.activeTable.unload();
            if (dbConnector.activeTable.dbPath.getFileName().toString().equals(args[0])) {
                System.out.println("using " + args[0]);
                return true;
            }
        }
        dbConnector.activeTable = map;
        System.out.println("using " + args[0]);
        return true;
    }

}
