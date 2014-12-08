package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

public class RemoveCommand implements Command {
    public void run(String[] args, JUnitTableProvider tableProvider)
            throws IllegalArgumentException {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        JUnitTable currentTable = tableProvider.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return;
        }
        if (!args[1].isEmpty()) {
            String value = currentTable.remove(args[1]);
            if (value == null) {
                System.out.println("not found");
                return;
            }
            System.out.println(value);

        }
    }
}
