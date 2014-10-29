package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;

import java.nio.file.Files;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Drop extends Command {

    public Drop() {
        super("drop", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            if (packageModeInInteractive) {
                return false;
            }
            return true;
        }

        MFHMap map = dbConnector.tables.get(args[0]);
        if (map == null) {
            System.out.println(args[0] + " not exists");
            if (packageModeInInteractive) {
                return false;
            }
            if (packageMode) {
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
            map.deleteFiles(true);
            Files.delete(map.dbPath);
        } catch (Exception e) {
            System.err.println("Exception in drop: can't delete " + map.dbPath.toString());
            System.exit(-1);
        }
        System.out.println("dropped");
        return true;
    }
}
