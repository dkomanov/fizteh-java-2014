package ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands;

import java.util.function.BiConsumer;

import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.Table;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.TableProvider;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.Command;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.CommandHandler;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.InterpreterState;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.StringInterpreterState;

public class CommandProviderCreate extends Command {
    
    public CommandProviderCreate() {
        name = "create";
        quantityOfArguments = 1;
    }
    
    @Override
    public CommandHandler create() {
        return new CommandHandler(name, quantityOfArguments, 
                new BiConsumer<InterpreterState, String[]>() {
                    @Override
                    public void accept(InterpreterState interpreterState, String[] args) {
                        StringInterpreterState state = (StringInterpreterState) interpreterState;
                        TableProvider tableProvider = state.getTableProvider();
                        
                        try {
                            tableProvider.getTable(args[0]);
                            System.out.println(name + " exists");
                        } catch (IllegalArgumentException e) {
                            Table table = tableProvider.createTable(args[0]);
                            if (table != null) {
                                System.out.println("created");
                            }
                        }   
                    }
                });
    }
}