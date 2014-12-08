package ru.fizteh.fivt.students.tonmit.JUnit;

public class List extends JUnitCommand {
    public List() {
        super("list", 0);
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
        java.util.List<String> keySet = dbConnector.activeTable.list();
        int count = 0;
        for (String key : keySet) {
            System.out.print(key);
            if (count != keySet.size() - 1) {
                System.out.print(", ");
                ++count;
            }
        }
        System.out.println();
        return true;
    }
}

