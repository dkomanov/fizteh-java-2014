package ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.commands;

import java.util.List;
import java.util.function.BiConsumer;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.Command;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.CommandHandler;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter.InterpreterState;
import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.StringInterpreterState;

public class CommandTableList extends Command {
    
    public CommandTableList() {
        name = "list";
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
                        
                        List<String> list = table.list();
                        int i = list.size();
                        for (String key : list) {
                            System.out.print(key);
                            --i;
                            if (i > 0) {
                                System.out.print(", ");
                            }
                        }
                        System.out.println();
                    }
                });
    }
}