package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.newJUnit;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Use extends JUnitCommand {

    public Use() {
        super("use", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        if (dbConnector.activeTable != null) {
            if (!dbConnector.activeTable.removed.isEmpty() || !dbConnector.activeTable.newKey.isEmpty()) {
                int count = 0;
                for (String s : dbConnector.activeTable.removed.keySet()) {
                    if (dbConnector.activeTable.activeTable.containsKey(s)) {
                        ++count;
                    }
                }
                count += dbConnector.activeTable.newKey.size();
                System.out.println(count + " unsaved changes");
                return true;
            }
        }
        CurrentTable map = dbConnector.tables.get(args[0]);
        if (map == null) {
            map = dbConnector.activeTableProvider.tables.get(args[0]);
        }
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
            dbConnector.activeTable.unload(dbConnector.activeTable, dbConnector.activeTable.getName());
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
