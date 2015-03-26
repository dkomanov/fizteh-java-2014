package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

public class Remove extends ParentCommand {

    private String key;

    public Remove(CommandState state) {
        super(state);
    }

    @Override
    public void run() {
        if (state.usingTable == null) {
            System.out.println("no table");
        } else {
            String result = state.usingTable.remove(key);
            if (result != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
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