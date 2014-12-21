package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Connector;

/**
 * Created by Дмитрий on 31.10.2014.
 */
public class Size extends StoreableCommand {
    public Size() {
        super("size", 0);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        int size = 0;
        for (String s : dbConnector.getTables().keySet()) {      // возможно что-то нужно будет убрать
            size += dbConnector.getTables().get(s).size();
        }
        for (String s : dbConnector.getActiveTableProvider().tables.keySet()) {
            size += dbConnector.getActiveTableProvider().tables.get(s).size();
        }
        System.out.println(size);
        return true;
    }
}
