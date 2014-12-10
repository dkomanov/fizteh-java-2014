package ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands;

import java.util.function.BiConsumer;

import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.Table;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.Command;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.CommandHandler;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.InterpreterState;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.StringInterpreterState;

public class CommandTablePut extends Command {
    
    public CommandTablePut() {
        name = "put";
        quantityOfArguments = 2;
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
                        
                        String value = table.put(args[0], args[1]);
                        if (value == null) {
                            System.out.println("new");
                        } else {
                            System.out.println("overwrite");
                            System.out.println(value);
                        }
                        state.setTable(table);
                    }                        
                });
    }
}