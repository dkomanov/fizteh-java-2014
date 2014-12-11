package ru.fizteh.fivt.students.deserg.telnet.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;
import ru.fizteh.fivt.students.deserg.telnet.Serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by deserg on 22.10.14.
 */
public class DbCreate implements DbCommand {

    @Override
    public String execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 2) {
            return "Not enough arguments";
        } else {

            String tableName = args.get(1);
            String typeString = "";
            for (int i = 2; i < args.size(); i++) {
                typeString += args.get(i) + " ";
            }

            String[] types = typeString.substring(1, typeString.length() - 2).split("\\s+");
            List<Class<?>> signature = Serializer.makeSignatureFromStrings(types);

            try {
                if (db.createTable(tableName, signature) == null) {
                    return tableName + " exists";
                } else {
                    return "created";
                }

            } catch (IOException ex) {
                return "IO error while creating the table \"" + tableName + "\"";
            }

        }

    }

}
