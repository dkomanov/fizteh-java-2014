package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.clientCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ClientLogic;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.UnknownCommandException;

import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */
public abstract class ClientCommand {
    private static final HashMap<String, ClientCommand> COMMANDS;

    static {
        COMMANDS = new HashMap<>();
        COMMANDS.put("connect", new ConnectCommand());
        COMMANDS.put("disconnect", new DisconnectCommand());
        COMMANDS.put("whereami", new WhereAmICommand());
    }

    public static ClientCommand fromString(String s) throws Exception {
        if (s.length() < 1) {
            throw new Exception("");
        }
        String[] tokens = s.split("\\s+", 0);
        if (COMMANDS.containsKey(tokens[0])) {
            ClientCommand command = COMMANDS.get(tokens[0]);
            if (tokens.length - 1 != command.numberOfArguments()) {
                throw new Exception("Unexpected number of arguments: " + command.numberOfArguments() + " required");
            }
            command.putArguments(tokens);
            return command;
        } else {
            throw new UnknownCommandException();
        }
    }

    public abstract void execute(ClientLogic base) throws Exception;
    protected void putArguments(String[] args) throws Exception {
    }
    protected abstract int numberOfArguments();
}
