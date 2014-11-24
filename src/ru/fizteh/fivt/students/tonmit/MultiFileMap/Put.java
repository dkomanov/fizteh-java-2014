package ru.fizteh.fivt.students.tonmit.MultiFileMap;

public class Put extends Command {

    public Put() {
        this.name = "put";
        this.argLen = 2;
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
        String value = dbConnector.currentTable.put(args[0], args[1]);
        if (value != null) {
            System.out.println("overwrite");
            System.out.println(value);
        } else {
            System.out.println("new");
        }
        String newPath = dbConnector.currentTable.whereToSave(args[0]).getKey();
        if (dbConnector.currentTable.changedFiles.containsKey(newPath)) {
            Integer collisionCount = dbConnector.currentTable.changedFiles.get(newPath);
            ++collisionCount;
            dbConnector.currentTable.changedFiles.put(newPath, collisionCount);
        } else {
            dbConnector.currentTable.changedFiles.put(newPath, 0);
        }
        return true;
    }
}
