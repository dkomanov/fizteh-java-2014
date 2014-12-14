package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

public class ExitCommand implements Command {
    public void run(String[] args, JUnitTableProvider tableProvider) {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        JUnitTable currentTable = tableProvider.getCurrentTable();
        if (currentTable != null) {
            currentTable.rollback();
        }
        System.exit(0);
    }
}
