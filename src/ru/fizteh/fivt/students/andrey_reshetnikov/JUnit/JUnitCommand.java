package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

import java.util.HashMap;

public abstract class JUnitCommand {
    protected static final int NUM_FILES = 16;
    protected static final int NUM_DIRECTORIES = 16;
    private static final HashMap<String, JUnitCommand> COMMANDS;

    static {
        COMMANDS = new HashMap<>();
        COMMANDS.put("create", new JUnitCreateCommand());
        COMMANDS.put("drop", new JUnitDropCommand());
        COMMANDS.put("use", new JUnitUseCommand());
        COMMANDS.put("show_tables", new JUnitShowTablesCommand());
        COMMANDS.put("put", new JUnitPutCommand());
        COMMANDS.put("get", new JUnitGetCommand());
        COMMANDS.put("remove", new JUnitRemoveCommand());
        COMMANDS.put("list", new JUnitListCommand());
        COMMANDS.put("exit", new ExitCommand());
        COMMANDS.put("commit", new CommitCommand());
        COMMANDS.put("rollback", new RollBackCommand());
        COMMANDS.put("size", new SizeCommand());
    }

    public static JUnitCommand fromString(String s) throws Exception {
        if (s.length() < 1) {
            throw new Exception("Empty command");
        }
        if (s.length() > 4 && s.substring(0, 5).equals("show ")) {
            s = s.replaceFirst(" ", "_");
        }
        String[] tokens = s.split("\\s+", 0);
        if (COMMANDS.containsKey(tokens[0])) {
            JUnitCommand command = COMMANDS.get(tokens[0]);
            if (tokens.length - 1 != command.numberOfArguments()) {
                throw new Exception("Unexpected number of arguments: " + command.numberOfArguments() + " required");
            }
            command.putArguments(tokens);
            return command;
        } else {
            throw new Exception(tokens[0] + " is unknown command");
        }
    }

    public abstract void execute(JUnitDataBaseDir base) throws Exception;

    protected void putArguments(String[] args) {}

    protected abstract int numberOfArguments();
}
