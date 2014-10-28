package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */
public abstract class Command {

    private static final HashMap<String, Command> COMMANDS;

    static {
        COMMANDS = new HashMap<>();
        COMMANDS.put("put", new PutCommand());
        COMMANDS.put("get", new GetCommand());
        COMMANDS.put("remove", new RemoveCommand());
        COMMANDS.put("list", new ListCommand());
        COMMANDS.put("exit", new ExitCommand());
    }

    public static Command fromString(String s) throws Exception {
        if (s.length() < 1) {
            throw new Exception("Empty command");
        }
        String[] tokens = s.split("\\s+", 0);
        if (COMMANDS.containsKey(tokens[0])) {
            Command command = COMMANDS.get(tokens[0]);
            if (tokens.length - 1 != command.numberOfArguments()) {
                throw new Exception("Unexpected number of arguments " + command.numberOfArguments() + " required");
            } else {
                command.putArguments(tokens);
                return command;
            }
        } else {
            throw new Exception(tokens[0] + " is unknown command");
        }
    }

    public abstract void execute(DataBase base) throws Exception;
    public void putArguments(String[] args) {
    }
    protected abstract int numberOfArguments();
}
