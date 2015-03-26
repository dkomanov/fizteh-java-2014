package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

public class Size extends ParentCommand {

    public Size(CommandState state) {
        super(state);
    }

    @Override
    public void run() {
        if (state.usingTable == null) {
            System.out.println("no table");
        } else {
            System.out.println(state.usingTable.size());
        }
    }

    @Override
    public int requiredArgsNum() {
        return 0;
    }
}
