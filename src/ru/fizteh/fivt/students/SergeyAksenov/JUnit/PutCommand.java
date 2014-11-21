package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

public class PutCommand implements Command {
    public void run(String[] args, JUnitTableProvider tableProvider) throws IllegalArgumentException {
        if (!Executor.checkArgNumber(3, args.length, 3)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        JUnitTable currentTable = tableProvider.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return;
        }
        String oldValue = currentTable.put(args[1], args[2]);
        if (oldValue != null) {
            System.out.println("new");
            return;
        }
        System.out.println("overwrite");
        System.out.println(oldValue);
    }
}