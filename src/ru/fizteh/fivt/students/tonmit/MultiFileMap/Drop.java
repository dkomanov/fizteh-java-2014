package ru.fizteh.fivt.students.tonmit.MultiFileMap;

import java.nio.file.Files;

public class Drop extends Command {

    public Drop() {
        this.name = "drop";
        this.argLen = 1;
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return false;
        }

        MFHMap map = dbConnector.tables.get(args[0]);
        if (map == null) {
            System.out.println(args[0] + " not exists");
            if (packageMode) {
                System.exit(-1);
            }
            return false;
        }
        if (dbConnector.currentTable == map) {
            dbConnector.tables.remove(args[0]);
            dbConnector.currentTable = null;
        }
        try {
            if (dbConnector != null) {
                dbConnector.tables.remove(args[0]);
            }
            map.clear();
            map.deleteAllFiles();
            Files.delete(map.dbPath);
        } catch (Exception e) {
            System.err.println("Exception in drop: can't delete " + map.dbPath.toString());
            System.exit(-1);
        }
        System.out.println("dropped");
        return true;
    }
}
