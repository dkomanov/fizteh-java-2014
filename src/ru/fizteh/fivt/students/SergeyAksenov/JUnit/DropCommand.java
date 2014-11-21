package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

public class DropCommand implements Command {
    public void run(String[] args, JUnitTableProvider tableProvider) {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        try {
            tableProvider.removeTable(args[1]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}