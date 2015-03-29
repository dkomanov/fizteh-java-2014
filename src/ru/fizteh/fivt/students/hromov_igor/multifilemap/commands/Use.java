package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.DBaseTable;

public class Use extends ParentCommand {

    private String tableName;

    public Use(CommandState state) {
        super(state);
    }


    @Override
    public void run() {

        if (state.getBase().getTable(tableName) == null) {
            System.out.println(tableName + " not exists");
        } else {
            state.setUsingTable((DBaseTable) state.getBase().getTable(tableName));
            System.out.println("using " + tableName);
        }
    }


    @Override
    public int requiredArgsNum() {
        return 1;
    }

    @Override
    public void putArguments(String[] args) {
        tableName = args[1];
    }
}
