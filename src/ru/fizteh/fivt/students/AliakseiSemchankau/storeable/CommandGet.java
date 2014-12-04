package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */

public class CommandGet implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {

        if (args.size() != 2) {
            throw new DatabaseException("incorrect number of arguments(get)");
        }

        if (dProvider.currentTableName == null) {
            System.out.println("choose a table at first");
            return;
        }
        Table dTable = dProvider.getTable(dProvider.currentTableName);

        if (dTable == null) {
            System.out.println("choose a table at first");
            return;
        }

        String key = args.elementAt(1);

        Storeable value = dTable.get(key);
        if (value == null) {
            System.out.println("not found");
            return;
        }

        SerializeFunctions serializer = new SerializeFunctions();
        String valueToPrint;
        try {
            valueToPrint = serializer.serialize(dTable, value);
        } catch (ParseException pexc) {
            throw new DatabaseException("can't serialize value for " + key);
        }
            System.out.println("found");
        System.out.println(valueToPrint);

    }

}
