package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

public class Drop extends ParentCommand {
    private String tableName;

    public Drop(CommandState state) {
        super(state);
    }


    @Override
    public void run() {
        try {
            if (state.getBase().getTable(tableName) == state.getUsingTable()) {
                state.setUsingTable(null);
            }
            state.getBase().removeTable(tableName);
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(tableName + " not exists");
        }
    }

    public final void putArguments(String[] args) {
        tableName = args[1];
    }

    public final int requiredArgsNum() {
        return 1;
    }
}
