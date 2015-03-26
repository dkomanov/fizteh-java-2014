package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

public class Commit extends ParentCommand {

    public Commit(CommandState state) {
        super(state);
    }

    @Override
    public void run() {
        if (state.usingTable == null) {
            System.out.println("no table");
        } else {
            System.out.println(state.usingTable.commit());
        }
    }

    @Override
    public int requiredArgsNum() {
        return 0;
    }
}