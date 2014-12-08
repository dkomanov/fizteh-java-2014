package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

public class CommitCommand implements Command {
    public void run(String[] args, JUnitTableProvider tableProvider) {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        JUnitTable table = tableProvider.getCurrentTable();
        if (table == null) {
            System.out.println("no table");
            return;
        }
        int keyNum = table.commit();
        System.out.println(keyNum + " keys saved.");

    }
}
