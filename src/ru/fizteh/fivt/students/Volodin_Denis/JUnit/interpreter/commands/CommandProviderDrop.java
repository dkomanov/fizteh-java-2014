package ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands;

import java.util.function.BiConsumer;

import ru.fizteh.fivt.students.Volodin_Denis.JUnit.strings.TableProvider;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.Command;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.CommandHandler;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.InterpreterState;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.StringInterpreterState;

public class CommandProviderDrop extends Command {
    
    public CommandProviderDrop() {
        name = "drop";
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
                            tableProvider.removeTable(args[0]);
                            System.out.println("dropped");
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getMessage());
                        } catch (IllegalStateException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                });
    }
}