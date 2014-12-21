package ru.fizteh.fivt.students.egor_belikov.Storeable.Commands;

import ru.fizteh.fivt.students.egor_belikov.Storeable.MyTableProvider;

/**
 * Created by egor on 13.12.14.
 */
public class Remove implements Command {
    @Override
    public void execute(String[] args, MyTableProvider myTableProvider) throws Exception {
        if (myTableProvider.currentTable.remove(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
