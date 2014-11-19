package ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter;

import java.util.function.BiConsumer;

import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.ErrorFunctions;

public class CommandHandler {
    
    private String name;
    private int quantityOfArguments;
    private BiConsumer<InterpreterState, String[]> callback;

    public CommandHandler(String name, int quantityOfArguments, 
            BiConsumer<InterpreterState, String[]> callback) {
        this.name = name;
        this.quantityOfArguments = quantityOfArguments;
        this.callback = callback;
    }

    public CommandHandler(final String name, final int quantityOfArguments) {
        this.name = name;
        this.quantityOfArguments = quantityOfArguments;
    }
    
    public String getName() {
        return name;
    }
    
    private void checkQuantityOfArguments(final String[] args,
            final String commandName) throws Exception {
        if (args.length != quantityOfArguments) {
            ErrorFunctions.wrongQuantityOfArguments(commandName);
        }
        if (args.length > 1) {
            for (int i = 1; i < args.length; ++i) {
                if (args[i].isEmpty()) {
                    ErrorFunctions.wrongInput(commandName);
                }
            }
        }
    }
    
    public void execute(InterpreterState interpreterState, String[] args) throws Exception {
        checkQuantityOfArguments(args, name);
        
        callback.accept(interpreterState, args);
    }
}
