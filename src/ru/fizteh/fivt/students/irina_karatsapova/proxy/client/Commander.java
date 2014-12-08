package ru.fizteh.fivt.students.irina_karatsapova.proxy.client;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.client.commands.Command;

import java.util.HashMap;

public class Commander {
    
    private HashMap<String, Command> allCommands = new HashMap<>();
    private Command defaultCommand;
    
    public void addCommand(Command command) {
        allCommands.put(command.name(), command);
    }

    void setDefaultCommand(Command command) {
        defaultCommand = command;
    }

    public void startCommand(InterpreterState state, String commandWithArgs) throws Exception {
        String[] args = commandWithArgs.trim().split("\\s+");
        if (args[0].isEmpty()) {
            return;
        }
        Command command = allCommands.getOrDefault(args[0], defaultCommand);
        
        if ((command.minArgs() > args.length) || (command.maxArgs() < args.length)) {
            throw new Exception(command.name() + ": Wrong number of arguments");
        }
        try {
            command.execute(state, args);
        } catch (Exception e) {
            throw new Exception(command.name() + ": " + e.getMessage());
        }
    }
}


