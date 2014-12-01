package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.newJUnit;

import java.nio.file.Files;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Drop extends JUnitCommand {

    public Drop() {
        super("drop", 1);
    }

    @Override
    public  boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }

        boolean inProvider = true;
        CurrentTable map = dbConnector.activeTableProvider.tables.get(args[0]);
        if (map == null) {
            map = dbConnector.tables.get(args[0]);
            inProvider = false;
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
        if (dbConnector.activeTable == map) {
            if (inProvider) {
                dbConnector.activeTableProvider.tables.remove(args[0]);
            } else {
                dbConnector.tables.remove(args[0]);
            }
            dbConnector.activeTable = null;
        }
        try {
            if (dbConnector != null) {
                if (inProvider) {
                    dbConnector.activeTableProvider.tables.remove(args[0]);
                } else {
                    dbConnector.tables.remove(args[0]);
                }
            }
            map.clear();
            if (dbConnector.activeTable == null) {
                map.deleteFiles("", true);
            } else {
                map.deleteFiles(dbConnector.activeTable.getName(), true);
            }
            Files.delete(map.dbPath);
        } catch (Exception e) {
            System.err.println("Exception in drop: can't delete " + map.dbPath.toString());
            System.exit(-1);
        }
        System.out.println("dropped");
        return true;
    }
}
