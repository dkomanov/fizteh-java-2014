package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

public class Put extends ParentCommand {

    private String key;
    private String value;

    public Put(CommandState state) {
        super(state);
    }

    @Override
    public void run() {
        if (state.getUsingTable() == null) {
            System.out.println("no table");
        } else {
            String result = null;
            result = state.getUsingTable().put(key, value);
            if (result == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(result);
            }
        }
    }

    @Override
    public void putArguments(String[] args) {
        key = args[1];
        value = args[2];
    }


    @Override
    public int requiredArgsNum() {
        return 2;
    }

}
