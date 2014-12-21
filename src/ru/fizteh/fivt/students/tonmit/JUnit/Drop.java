package ru.fizteh.fivt.students.tonmit.JUnit;

import java.nio.file.Files;

public class Drop extends JUnitCommand {

    public Drop() {
        super("drop", 1);
    }

    @Override
    public  boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }

        CurrentTable map = dbConnector.activeTableProvider.tables.get(args[0]);
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
            dbConnector.tables.remove(args[0]);
            dbConnector.activeTable = null;
        }
        try {
            if (dbConnector != null) {
                dbConnector.tables.remove(args[0]);
            }
            map.clear();
            map.deleteFiles(dbConnector.activeTable.getName(), true);
            Files.delete(map.dbPath);
        } catch (Exception e) {
            System.err.println("Exception. drop: can't delete " + map.dbPath.toString());
            System.exit(-1);
        }
        System.out.println("dropped");
        return true;
    }
}
