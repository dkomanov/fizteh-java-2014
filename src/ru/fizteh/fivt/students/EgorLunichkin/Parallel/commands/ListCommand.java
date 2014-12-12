package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

import java.util.List;

public class ListCommand implements Command {
    public ListCommand() {}

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

    @Override
    public void putArguments(ParallelTableProvider ptp, String[] args) throws ParallelException {
        if (args.length > maxArguments()) {
            throw new ParallelException("list: Too many arguments");
        }
        base = ptp;
    }

    @Override
    public int minArguments() {
        return 0;
    }

    @Override
    public int maxArguments() {
        return 0;
    }
}
