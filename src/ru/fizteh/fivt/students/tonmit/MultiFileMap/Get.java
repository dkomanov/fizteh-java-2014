package ru.fizteh.fivt.students.tonmit.MultiFileMap;

public class Get extends Command {

    public Get() {
        this.name = "get";
        this.argLen = 1;
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return false;
        }
        if (dbConnector.currentTable == null) {
            noTable();
            return false;
        }
        String value = dbConnector.currentTable.get(args[0]);
        if (value != null) {
            System.out.println("found");
            System.out.println(value);
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
