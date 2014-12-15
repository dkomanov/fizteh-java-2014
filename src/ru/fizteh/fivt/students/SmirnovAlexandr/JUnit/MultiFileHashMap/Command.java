package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap;

import java.util.HashMap;

public abstract class Command {
    private static final HashMap<String, Command> COMMANDS;

    static {
        COMMANDS = new HashMap<>();
        COMMANDS.put("create", new Create());
        COMMANDS.put("drop", new Drop());
        COMMANDS.put("use", new Use());
        COMMANDS.put("show_tables", new ShowTables());
        COMMANDS.put("put", new MultiPut());
        COMMANDS.put("get", new MultiGet());
        COMMANDS.put("remove", new MultiRemove());
        COMMANDS.put("list", new MultiList());
        COMMANDS.put("exit", new Exit());
    }

    public static Command fromString(String s) throws Exception {
        return vocabularyGetter(s, COMMANDS);
    }

    public static Command vocabularyGetter(String s, HashMap<String, Command> commands) throws Exception {
        if (s.length() < 1) {
            throw new ExceptionEmptyCommand();
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

    public void executeOnTable(MultiTable table) throws Exception {}
}
