package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

import java.util.List;

public class ListCommand implements Command {
    public ListCommand(ParallelTableProvider ptp) {
        base = ptp;
    }

    private ParallelTableProvider base;

    @Override
    public void run() {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            List<String> listKeys = ((ParallelTable) base.getUsing()).list();
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
