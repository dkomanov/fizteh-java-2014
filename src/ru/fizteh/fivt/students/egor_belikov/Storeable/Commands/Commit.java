package ru.fizteh.fivt.students.egor_belikov.Storeable.Commands;

import ru.fizteh.fivt.students.egor_belikov.Storeable.MyTableProvider;

/**
 * Created by egor on 13.12.14.
 */
public class Commit implements Command {
    @Override
    public void execute(String[] args, MyTableProvider myTableProvider) throws Exception {
        System.out.println(myTableProvider.currentTable.commit());

    }
}
