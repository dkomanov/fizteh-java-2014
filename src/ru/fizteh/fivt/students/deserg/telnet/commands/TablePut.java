package ru.fizteh.fivt.students.deserg.telnet.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.deserg.telnet.server.DbTable;
import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;
import ru.fizteh.fivt.students.deserg.telnet.Serializer;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class TablePut implements DbCommand {

    @Override
    public String execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 3) {
            return "Not enough arguments";
        } else {

            DbTable table = db.getCurrentTable();
            if (table == null) {
                return "no table";
            }

            String key = args.get(1);
            String value = "";
            for (int i = 2; i < args.size(); i++) {
                value += args.get(i);
            }

            Storeable mValue;

            try {
                mValue = Serializer.deserialize(table, value);
            } catch (ParseException ex) {
                return "wrong type " + ex.getMessage();
            }

            if (table.put(key, mValue) != null) {
                return "overwrite";
            } else {
                return "new";

            }

        }

    }
}
