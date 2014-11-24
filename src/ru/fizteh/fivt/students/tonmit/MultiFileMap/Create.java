package ru.fizteh.fivt.students.tonmit.MultiFileMap;

public class Create extends Command {

    public Create() {
        this.name = "create";
        this.argLen = 1;
    }

    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return false;
        }
        MFHMap map = new MFHMap(dbConnector.dbRootDirectory.resolve(args[0]));
        if (dbConnector.tables.containsKey(args[0])) {
            System.out.println(args[0] + " exists");
        } else {
            dbConnector.tables.put(args[0], map);
            System.out.println("created");
        }
        return true;
    }
}
