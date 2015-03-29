package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBaseTable;

import java.util.Map;

public class ShowTables extends ParentCommand {


    public ShowTables(CommandState state) {
        super(state);
    }

    @Override
    public void run() {
        for (Map.Entry<String, DBaseTable> entry: state.getBase().entrySet()) {
            String name = entry.getKey();
            int size = entry.getValue().size();
            System.out.println(name + " " + size);
        }
    }

    @Override
    public int requiredArgsNum() {
        return 1;
    }
}
