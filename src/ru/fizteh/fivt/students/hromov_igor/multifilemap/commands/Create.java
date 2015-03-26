package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

public class Create extends ParentCommand {

    private String tableName;

    public Create(CommandState state) {
        super(state);
    }


    @Override
    public void run() {
        if (state.base.createTable(tableName) == null) {
            System.out.println(tableName + " exists");
        } else {
            System.out.println("created");
        }
    }

    public final void putArguments(String[] args) {
        tableName = args[1];
    }

    public final int requiredArgsNum() {
        return 1;
    }
}
