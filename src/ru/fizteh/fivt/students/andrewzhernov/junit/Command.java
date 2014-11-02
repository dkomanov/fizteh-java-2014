package ru.fizteh.fivt.students.andrewzhernov.junit;

public class Command {
    private String name;
    private int numArgs;
    private AbstractHandler handler;

    public Command(String name, int numArgs, AbstractHandler handler) {
        this.name = name;
        this.numArgs = numArgs;
        this.handler = handler;
    }

    public String getName() {
        return name;
    }

    public void exec(TableProvider database, String[] params) throws Exception {
        if (params.length != numArgs) {
            throw new Exception(String.format("Invalid number of arguments: %d expected, %d found.", numArgs, params.length));
        } else {
            handler.print(handler.exec(database, params));
        }
    }
}
