package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Connector;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.StoreableTable;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Use extends StoreableCommand {

    public Use() {
        super("use", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        if (dbConnector.getActiveTable() != null) {
            if (!dbConnector.getActiveTable().getRemoved().isEmpty()
                    || !dbConnector.getActiveTable().getNewKey().isEmpty()) {
                int count = 0;
                for (String s : dbConnector.getActiveTable().getRemoved().keySet()) {
                    if (dbConnector.getActiveTable().getActiveTable().containsKey(s)) {
                        ++count;
                    }
                }
                count += dbConnector.getActiveTable().getNewKey().size();
                System.out.println(count + " unsaved changes");
                return true;
            }
        }
        StoreableTable map = dbConnector.getActiveTableProvider().tables.get(args[0]);
        if (map == null) {
            map = dbConnector.getTables().get(args[0]);
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
        if (dbConnector.getActiveTable() != null) {
            dbConnector.getActiveTable().unload(dbConnector.getActiveTable(), dbConnector.getActiveTable().getName());
            if (dbConnector.getActiveTable().dbPath.getFileName().toString().equals(args[0])) {
                System.out.println("using " + args[0]);
                return true;
            }
        }
        dbConnector.setActiveTable(map);
        System.out.println("using " + args[0]);
        return true;
    }

}
