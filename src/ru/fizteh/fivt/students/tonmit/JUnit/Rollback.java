package ru.fizteh.fivt.students.tonmit.JUnit;

public class Rollback extends JUnitCommand {
    public Rollback() {
        super("rollback", 0);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
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
        System.out.println(dbConnector.activeTable.rollback());
        return true;
    }
}
