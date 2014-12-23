package ru.fizteh.fivt.students.egor_belikov.Storeable.Commands;

import ru.fizteh.fivt.students.egor_belikov.Storeable.MyTable;
import ru.fizteh.fivt.students.egor_belikov.Storeable.MyTableProvider;
import ru.fizteh.fivt.students.egor_belikov.Storeable.StoreableMain;

/**
 * Created by egor on 13.12.14.
 */
public class Get implements Command {
    @Override
    public void execute(String[] args, MyTableProvider myTableProvider) throws Exception {
        MyTable toGetFrom = (MyTable) myTableProvider.getTable(StoreableMain.currentTable);
        if (toGetFrom.get(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(myTableProvider.serialize(toGetFrom, toGetFrom.get(args[1])));
        }
    }
}
