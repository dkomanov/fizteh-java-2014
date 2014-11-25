package ru.fizteh.fivt.students.tonmit.MultiFileMap;

import java.util.Map;

public class Show extends Command {

    public Show() {
        this.name = "show";
        this.argLen = 1;
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return false;
        }
        if (!args[0].equals("tables")) {
            System.err.println("Bad show tables command.");
            if (packageMode) {
                System.exit(-1);
            }
            return false;
        }
        if (dbConnector.tables.isEmpty()) {
            return true;
        }
        String s = "";
        for (Map.Entry<String, MFHMap> a : dbConnector.tables.entrySet()) {
            s = a.getKey() + " " + a.getValue().size() + "\n";
            if (!s.substring(0, s.length() - 1).equals("")) {
                System.out.println(s.substring(0, s.length() - 1));
            }
        }
        return true;
    }
}
