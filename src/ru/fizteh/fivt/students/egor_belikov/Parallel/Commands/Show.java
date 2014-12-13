package ru.fizteh.fivt.students.egor_belikov.Parallel.Commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.egor_belikov.Parallel.MyTable;
import ru.fizteh.fivt.students.egor_belikov.Parallel.MyTableProvider;

import java.util.Map;

/**
 * Created by egor on 13.12.14.
 */
public class Show implements Command
{
    @Override
    public void execute(String[] args, MyTableProvider myTableProvider) throws Exception {
        if (!args[1].equals("tables")) {
            throw new Exception("show: invalid arguments");
        }
        System.out.println("table_name row_count");
        for (Map.Entry<String, Table> i : myTableProvider.listOfTables.entrySet()) {
            String key = i.getKey();
            int num = ((MyTable) (i.getValue())).numberOfElements;
            System.out.println(key + " " + num);
        }
    }
}
