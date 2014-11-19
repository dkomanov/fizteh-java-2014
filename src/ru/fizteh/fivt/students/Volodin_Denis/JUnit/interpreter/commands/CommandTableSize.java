package ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands;

import java.util.function.BiConsumer;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.Command;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.CommandHandler;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.InterpreterState;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.StringInterpreterState;

public class CommandTableSize extends Command {
    
    public CommandTableSize() {
        name = "size";
        quantityOfArguments = 0;
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
                        
                        System.out.println(table.size());
                    }
                });
    }
}