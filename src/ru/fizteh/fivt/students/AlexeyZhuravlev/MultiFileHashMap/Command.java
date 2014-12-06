package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */
public abstract class Command {

    private static final HashMap<String, Command> COMMANDS;

    static {
        COMMANDS = new HashMap<>();
        COMMANDS.put("create", new CreateCommand());
        COMMANDS.put("drop", new DropCommand());
        COMMANDS.put("use", new UseCommand());
        COMMANDS.put("show_tables", new ShowTablesCommand());
        COMMANDS.put("put", new MultiPutCommand());
        COMMANDS.put("get", new MultiGetCommand());
        COMMANDS.put("remove", new MultiRemoveCommand());
        COMMANDS.put("list", new MultiListCommand());
        COMMANDS.put("exit", new ExitCommand());
    }

    public static Command fromString(String s) throws Exception {
        if (s.length() < 1) {
            throw new Exception("");
        }
        if (s.length() > 4 && s.substring(0, 5).equals("show ")) {
            s = s.replaceFirst(" ", "_");
        }
        String[] tokens = s.split("\\s+", 0);
        if (COMMANDS.containsKey(tokens[0])) {
            Command command = COMMANDS.get(tokens[0]);
            if (tokens.length - 1 != command.numberOfArguments()) {
                throw new Exception("Unexpected number of arguments: " + command.numberOfArguments() + " required");
            }
            command.putArguments(tokens);
            return command;
        } else {
            throw new Exception(tokens[0] + " is unknown command");
        }
    }

    public abstract void execute(DataBaseDir base) throws Exception;
    protected void putArguments(String[] args) {
    }
    protected abstract int numberOfArguments();
}
