package ru.fizteh.fivt.students.tonmit.JUnit;

public class Size extends JUnitCommand {
    public Size() {
        super("size", 0);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        int size = 0;
        for (String s : dbConnector.tables.keySet()) {
            size += dbConnector.tables.get(s).size();
        }
        System.out.println(size);
        return true;
    }
}
