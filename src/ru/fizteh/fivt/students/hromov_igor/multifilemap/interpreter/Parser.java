package ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.commands.ParentCommand;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class Parser {

    public static void parseCommand(String[] commands, HashMap<String, ParentCommand> listCommands) {
        try {
            ParentCommand command = null;
            if (commands[0].equals("")) {
                throw new NoSuchElementException();
            }
            if (commands[0].equals("show")) {
                commands[0] += "_" + commands[1];
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
