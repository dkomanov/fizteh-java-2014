package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

import java.util.List;

public class ListCommand implements Command {
    public void run(String[] args, JUnitTableProvider tableProvider) throws IllegalArgumentException {
        if (!Executor.checkArgNumber(1, args.length, 1)) {
            System.out.println("Invalid number of arguments");
            return;
        }
        JUnitTable currentTable = tableProvider.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return;
        }
        List<String> list = currentTable.list();
        for (String key : list) {
            System.out.print(key + ",");
        }
        System.out.println();
    }
}
