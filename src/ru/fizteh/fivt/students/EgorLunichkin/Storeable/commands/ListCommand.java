package ru.fizteh.fivt.students.EgorLunichkin.Storeable.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTable;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTableProvider;

import java.util.List;

public class ListCommand implements Command {
    public ListCommand(StoreableTableProvider stp) {
        sTableProvider = stp;
    }

    private StoreableTableProvider sTableProvider;

    @Override
    public void run() {
        if (sTableProvider.getUsing() == null) {
            System.out.println("no table");
        } else {
            List<String> listKeys = ((StoreableTable) sTableProvider.getUsing()).list();
            StringBuilder list = new StringBuilder();
            for (String key : listKeys) {
                if (list.length() > 0) {
                    list.append(", ");
                }
                list.append(key);
            }
            System.out.println(list.toString());
        }
    }
}
