package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

public class Commit extends ParentCommand {

    public Commit(CommandState state) {
        super(state);
    }

    @Override
    public void run() {
        if (state.getUsingTable() == null) {
            System.out.println("no table");
        } else {
            System.out.println(state.getUsingTable().commit());
        }
    }

    @Override
    public int requiredArgsNum() {
        return 0;
    }
}
