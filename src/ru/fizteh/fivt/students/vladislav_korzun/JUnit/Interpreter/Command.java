package ru.fizteh.fivt.students.vladislav_korzun.JUnit.Interpreter;

import java.util.function.BiConsumer;

public class Command {
    private String name;
    private BiConsumer<Object, String[]> callback;
    private int numberOfArgs;
    
    public Command(String name, int numberOfArgs, BiConsumer<Object, String[]> callback) {
        this.name = name;
        this.callback = callback;
        this.numberOfArgs = numberOfArgs;
    }
    
   
    public String getName() {
        return name;
    }
    
    public final void execute(Object connector, String[] args) throws Exception {
        if (args.length != numberOfArgs) {
            throw new Exception(name + ": Incorrect number of arguments: " 
                    + numberOfArgs + " expected, but " + args.length + " found.");
        } else {
            callback.accept(connector, args);
        }
    }
}
