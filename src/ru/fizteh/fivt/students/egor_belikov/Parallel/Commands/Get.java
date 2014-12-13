package ru.fizteh.fivt.students.egor_belikov.Parallel.Commands;

import ru.fizteh.fivt.students.egor_belikov.Parallel.MyTableProvider;

/**
 * Created by egor on 13.12.14.
 */
public class Get implements Command {
    @Override
    public void execute(String[] args, MyTableProvider myTableProvider) throws Exception {
        if (myTableProvider.currentTable.get(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(myTableProvider.serialize(MyTableProvider.currentTable,
                    MyTableProvider.currentTable.get(args[1])));
        }
    }
}
