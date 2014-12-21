package ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands;

import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.Command;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.CommandHandler;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.InterpreterState;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.StringInterpreterState;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.Table;

import java.util.function.BiConsumer;

public class CommandTableGet extends Command {
    
    public CommandTableGet() {
        name = "get";
        quantityOfArguments = 1;
    }
    
    @Override
    public CommandHandler create() {
        return new CommandHandler(name, quantityOfArguments, 
                new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] args) {
                        StringInterpreterState state = (StringInterpreterState) interpreterState;
                        Table table = state.getTable();
                        try {
                            tableIsNull(table);
                        } catch (Exception e) {
                            return;
                        }
                        
                        String value = table.get(args[0]);
                        if (value == null) {
                            System.out.println("not found");
                        } else {
                            System.out.println("found");
                            System.out.println(value);
                        }
                    }
                });
    }
}
