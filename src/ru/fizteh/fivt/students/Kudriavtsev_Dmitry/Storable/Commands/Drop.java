package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Connector;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.StoreableTable;

import java.io.File;
import java.nio.file.Files;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Drop extends StoreableCommand {

    public Drop() {
        super("drop", 1);
    }

    @Override
    public  boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }

        boolean inProvider = true;
        StoreableTable map = dbConnector.getActiveTableProvider().tables.get(args[0]);
        if (map == null) {
            map = dbConnector.getTables().get(args[0]);
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
        if (dbConnector.getActiveTable() == map) {
            if (inProvider) {
                dbConnector.getActiveTableProvider().tables.remove(args[0]);
            } else {
                dbConnector.getTables().remove(args[0]);
            }
            dbConnector.setActiveTable(null);
        }
        try {
            if (dbConnector != null) {
                if (inProvider) {
                    dbConnector.getActiveTableProvider().tables.remove(args[0]);
                } else {
                    dbConnector.getTables().remove(args[0]);
                }
            }
            if (dbConnector.getActiveTable() == null) {
                map.deleteFiles("", true);
                Files.delete(new File(map.dbPath + File.separator + "signature.tsv").toPath());
            } else {
                map.deleteFiles(dbConnector.getActiveTable().getName(), true);
            }
            Files.delete(map.dbPath);
        } catch (Exception e) {
            System.err.println("Exception in drop: can't delete " + map.dbPath.toString()
                                                    + " because " + e.getLocalizedMessage());
            System.exit(-1);
        }
        System.out.println("dropped");
        return true;
    }
}
