package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.util.HashMap;

public abstract class Command {
    protected static final int NUM_FILES = 16;
    protected static final int NUM_DIRECTORIES = 16;

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
        return vocabularyGetter(s, COMMANDS);
    }

    public static Command vocabularyGetter(String s, HashMap<String, Command> commands) throws Exception {
        if (s.length() < 1) {
            throw new Exception("Empty command");
        }
        if (s.length() > 4 && s.substring(0, 5).equals("show ")) {
            s = s.replaceFirst(" ", "_");
        }
        String[] tokens = s.split("\\s+", 0);
        if (commands.containsKey(tokens[0])) {
            Command command = commands.get(tokens[0]);
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

    protected void putArguments(String[] args) {}

    protected abstract int numberOfArguments();

    public void executeOnTable(Table table) throws Exception {}
}
