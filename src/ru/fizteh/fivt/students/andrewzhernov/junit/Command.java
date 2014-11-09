package ru.fizteh.fivt.students.andrewzhernov.junit;

public class Command {
    private String name;
    private int numArgs;
    private HandlerInterface processor;

    public Command(String name, int numArgs, HandlerInterface processor) {
        this.name = name;
        this.numArgs = numArgs;
        this.processor = processor;
    }

    public String getName() {
        return name;
    }

    public void execute(TableProvider database, String[] params) throws Exception {
        if (params.length != numArgs) {
            throw new Exception(String.format("Invalid number of arguments: %d expected, %d found.",
                                              numArgs, params.length));
        } else {
            processor.handle(processor.execute(database, params));
        }
    }
}
