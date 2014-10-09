package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;

import java.util.Map;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Show extends Command {

    public Show() {
        super("show", 1);
    }

    @Override
    public boolean exec(Connector dbConnector ,String[] args) {
        if (args.length != argLen) {
            System.err.println("Incorrect number of arguments in " + name);
            return false;
        }

        if (dbConnector.tables.isEmpty()) {
            System.out.println();
            return true;
        }
        String s = "";
        for( Map.Entry<String, MFHMap> a: dbConnector.tables.entrySet() ) {
            s = a.getKey() + " " + a.getValue().size() + "\\n";
        }
        System.out.println(s.substring(0, s.length() - 1));
        return true;
    }
}
