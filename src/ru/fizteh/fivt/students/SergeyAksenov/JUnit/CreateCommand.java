package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

public class CreateCommand implements Command {
    public void run(String[] args, JUnitTableProvider tableProvider) {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            System.out.println("Invalid number of arguments");
        }
        if (tableProvider.createTable(args[1]) == null) {
            System.out.println(args[1] + " exists");
            return;
        }
        System.out.println("created");
    }
}
