package ru.fizteh.fivt.students.EgorLunichkin.Storeable.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTableProvider;

public class ShowTablesCommand implements Command {
    public ShowTablesCommand(StoreableTableProvider stp) {
        sTableProvider = stp;
    }

    private StoreableTableProvider sTableProvider;

    @Override
    public void run() {
        for (String tableName : sTableProvider.getTableNames()) {
            System.out.println(tableName + " " + sTableProvider.getTable(tableName).size());
        }
    }
}
