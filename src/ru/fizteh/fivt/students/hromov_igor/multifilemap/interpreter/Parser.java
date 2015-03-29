package ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter;


import ru.fizteh.fivt.students.hromov_igor.multifilemap.commands.Exit;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter.exception.ExitCommandException;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class Parser {

    public static void parseAndExecute(String[] commands, HashMap<String, BaseCommand> listCommands) throws ExitCommandException {
        try {
            BaseCommand command = null;
            if (commands[0].equals("")) {
                throw new NoSuchElementException();
            }

            if (listCommands.containsKey(commands[0])) {
                command = listCommands.get(commands[0]);
            }
            command.putArguments(commands);
            command.run();
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
        }
    }
}
