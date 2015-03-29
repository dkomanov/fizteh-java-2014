package ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter.exception.ExitCommandException;

import java.util.Map;
import java.util.NoSuchElementException;

public class Parser {

    public static void parseAndExecute(String[] commands, 
        Map<String, BaseCommand> listCommands) throws ExitCommandException {
        try {
            if (commands[0].equals("")) {
                throw new NoSuchElementException();
            }

            if (listCommands.containsKey(commands[0])) {
                BaseCommand command = null;
                if (commands[0].equals("show")) {
                    commands[0] += "_" + commands[1];
                }
                command = listCommands.get(commands[0]);
                command.putArguments(commands);
                command.run();
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
        }
    }
}
