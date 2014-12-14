package ru.fizteh.fivt.students.egor_belikov.Parallel.Commands;

import ru.fizteh.fivt.students.egor_belikov.Parallel.MyTable;
import ru.fizteh.fivt.students.egor_belikov.Parallel.MyTableProvider;
import ru.fizteh.fivt.students.egor_belikov.Parallel.ParallelMain;

/**
 * Created by egor on 13.12.14.
 */
public class Get implements Command {
    @Override
    public void execute(String[] args, MyTableProvider myTableProvider) throws Exception {
        MyTable toGetFrom = (MyTable) myTableProvider.getTable(ParallelMain.currentTable);
        if (toGetFrom.get(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(myTableProvider.serialize(toGetFrom, toGetFrom.get(args[1])));
        }
    }
}
