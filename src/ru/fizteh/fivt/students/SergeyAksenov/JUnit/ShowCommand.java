package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

public class ShowCommand implements Command {
    public void run(String[] args, JUnitTableProvider tableProvider) {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        String[] tables = tableProvider.getTableNames();
        for (String tableName : tables) {
            System.out.println(tableName);
        }
    }
}