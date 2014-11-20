package ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands;

import java.util.function.BiConsumer;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.Command;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.CommandHandler;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.InterpreterState;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.StringInterpreterState;

public class CommandProviderUse extends Command {
    
    public CommandProviderUse() {
        name = "use";
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
                        if (table.getName().equals(args[0])) {
                            System.out.println("[" + args[0] + "] already used");
                            return;
                        }
                        int changes = table.getNumberOfUncommittedChanges();
                        if (changes != 0) {
                            System.out.println(changes + " unsaved changes");
                            return;
                        }
                        try {
                            state.setTable(state.getTableProvider().getTable(args[0]));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });
    }
}