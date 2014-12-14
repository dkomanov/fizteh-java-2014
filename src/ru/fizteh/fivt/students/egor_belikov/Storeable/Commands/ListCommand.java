package ru.fizteh.fivt.students.egor_belikov.Storeable.Commands;

import ru.fizteh.fivt.students.egor_belikov.Storeable.MyTableProvider;

import java.util.List;

/**
 * Created by egor on 13.12.14.
 */
public class ListCommand implements Command {
    @Override
    public void execute(String[] args, MyTableProvider myTableProvider) throws Exception {
        int counter = 0;
        List<String> list = myTableProvider.currentTable.list();
        for (String current : list) {
            ++counter;
            System.out.print(current);
            if (counter != list.size()) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }
}
