package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.serverCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ServerLogic;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.UnknownCommandException;

import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */
public abstract class ServerCommand {
    private static final HashMap<String, ServerCommand> COMMANDS;

    static {
        COMMANDS = new HashMap<>();
        COMMANDS.put("start", new StartCommand());
        COMMANDS.put("stop", new StopCommand());
        COMMANDS.put("listuners", new ListunersCommand());
    }

    public static ServerCommand fromString(String s) throws Exception {
        if (s.length() < 1) {
            throw new Exception("");
        }
        String[] tokens = s.split("\\s+", 0);
        if (COMMANDS.containsKey(tokens[0])) {
            ServerCommand command = COMMANDS.get(tokens[0]);
            command.putArguments(tokens);
            return command;
        } else {
            throw new UnknownCommandException();
        }
    }

    public abstract void execute(ServerLogic base) throws Exception;
    protected void putArguments(String[] args) throws Exception {
    }
}
