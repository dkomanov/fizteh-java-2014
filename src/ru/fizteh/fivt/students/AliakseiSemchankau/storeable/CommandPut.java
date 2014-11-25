package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */
public class CommandPut implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {

        if (args.size() < 3) {
            throw new DatabaseException("incorrect number of arguments(put)");
        }

        String key = args.elementAt(1);
        String jSON = new String();
        for (int i = 2; i < args.size(); ++i) {
            jSON = jSON + args.elementAt(i);
        }

        if (key == null || jSON == null) {
            throw new IllegalArgumentException("arguments for put can't be nulls");
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

        SerializeFunctions serializer = new SerializeFunctions();

        ArrayList<Class<?>> signature = new ArrayList<>();
        for (int i = 0; i < dTable.getColumnsCount(); ++i) {
            Class<?> curClass = dTable.getColumnType(i);
            signature.add(curClass);
        }


        Storeable newValue;
        try {
            newValue = serializer.deserialize(signature, jSON);
        } catch (ParseException pexc) {
            throw new DatabaseException("can't deserialize " + jSON);
        }

        Storeable oldValue = dTable.put(key, newValue);

        // System.out.println("old: " + oldValue);

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            String oldValueToPrint;
            try {
                oldValueToPrint = serializer.serialize(dTable, oldValue);
            } catch (ParseException pexc) {
                throw new DatabaseException("can't serialize value for " + key);
            }
            System.out.println(oldValueToPrint);
        }
    }

}