package ru.fizteh.fivt.students.tonmit.MultiFileMap;

public class Use extends Command {

    public Use() {
        this.name = "use";
        this.argLen = 1;
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return false;
        }

        MFHMap map = dbConnector.tables.get(args[0]);
        if (map == null) {
            System.out.println(args[0] + " not exists");
            if (packageMode) {
                System.exit(-1);
            }
            return true;
        }
        if (dbConnector.currentTable != null) {
            dbConnector.currentTable.unload();
            if (dbConnector.currentTable.dbPath.getFileName().toString().equals(args[0])) {
                System.out.println("using " + args[0]);
                return true;
            }
        }
        dbConnector.currentTable = map;
        System.out.println("using " + args[0]);
        return true;
    }
}
