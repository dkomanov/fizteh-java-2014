package ru.fizteh.fivt.students.tonmit.MultiFileMap;

public class Remove extends Command {
    public Remove() {
        this.name = "remove";
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
        if (dbConnector.currentTable.remove(args[0]) != null) {
            System.out.println("removed");
            dbConnector.currentTable.changedFiles.put(dbConnector.currentTable.whereToSave(args[0]).getKey(), 0);
        } else {
            System.out.println("not found");
        }
        return true;
    }

}
