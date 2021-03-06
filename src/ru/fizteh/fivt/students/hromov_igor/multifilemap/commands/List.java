package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

public class List extends ParentCommand {

    public List(CommandState state) {
        super(state);
    }

    @Override
    public void run() {
        if (state.getUsingTable() == null) {
            System.out.println("no table");
        } else {
            System.out.println(String.join(", ", state.getUsingTable().list()));
        }
    }

    @Override
    public int requiredArgsNum() {
        return 0;
    }
}
