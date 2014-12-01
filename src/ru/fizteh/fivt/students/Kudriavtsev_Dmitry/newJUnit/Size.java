package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.newJUnit;

/**
 * Created by Дмитрий on 31.10.2014.
 */
public class Size extends JUnitCommand {
    public Size() {
        super("size", 0);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        int size = 0;
        for (String s : dbConnector.tables.keySet()) {
            size += dbConnector.tables.get(s).size();
        }
        for (String s : dbConnector.activeTableProvider.tables.keySet()) {
            size += dbConnector.activeTableProvider.tables.get(s).size();
        }
        System.out.println(size);
        return true;
    }
}
