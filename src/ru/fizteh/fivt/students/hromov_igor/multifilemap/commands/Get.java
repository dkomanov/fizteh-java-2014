package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

public class Get extends ParentCommand {

    private String key;

    public Get(CommandState state) {
        super(state);
    }

    @Override
    public void run() {
        if (state.getUsingTable() == null) {
            System.out.println("no table");
        } else {
            String result = state.getUsingTable().get(key);
            if (result == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(result);
            }
        }
    }

    public final void putArguments(String[] args) {
        key = args[1];
    }

    @Override
    public int requiredArgsNum() {
        return 1;
    }

}
