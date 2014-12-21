package ru.fizteh.fivt.students.egor_belikov.Parallel.Commands;

import ru.fizteh.fivt.students.egor_belikov.Parallel.MyTable;
import ru.fizteh.fivt.students.egor_belikov.Parallel.MyTableProvider;

/**
 * Created by egor on 13.12.14.
 */
public class Use implements Command {
    @Override
    public void execute(String[] args, MyTableProvider myTableProvider) throws Exception {
        if (myTableProvider.listOfTables.get(args[1]) == null) {
            System.out.println(args[1] + " not exists");
        } else {
            if (myTableProvider.currentTable != null && myTableProvider.currentTable.numberOfUnsavedChanges > 0) {
                System.out.println(MyTableProvider.currentTable.numberOfUnsavedChanges + " unsaved changes");
            } else {
                myTableProvider.currentTable = (MyTable) myTableProvider.listOfTables.get(args[1]);
                System.out.println("using " + args[1]);
            }
        }
    }
}
