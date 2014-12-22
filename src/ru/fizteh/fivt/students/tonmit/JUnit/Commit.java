package ru.fizteh.fivt.students.tonmit.JUnit;

public class Commit extends JUnitCommand {
    public Commit() {
        super("commit", 0);
    }

    @Override
    public  boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        if (dbConnector.activeTable == null) {
            if (batchModeInInteractive) {
                System.err.println("No table");
                return false;
            }
            noTable();
            return true;
        }
        System.out.println(dbConnector.activeTable.commit());
        return true;
    }
}
