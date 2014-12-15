package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

import java.util.HashMap;

public abstract class JCommand {
    private static final HashMap<String, JCommand> COMMANDS;

    static {
        COMMANDS = new HashMap<>();
        COMMANDS.put("create", new JCreateCommand());
        COMMANDS.put("drop", new JDropCommand());
        COMMANDS.put("use", new JUseCommand());
        COMMANDS.put("show_tables", new JShowTablesCommand());
        COMMANDS.put("put", new JPutCommand());
        COMMANDS.put("get", new JGetCommand());
        COMMANDS.put("remove", new JRemoveCommand());
        COMMANDS.put("list", new JListCommand());
        COMMANDS.put("exit", new JExitCommand());
        COMMANDS.put("commit", new JCommitCommand());
        COMMANDS.put("rollback", new JRollBackCommand());
        COMMANDS.put("size", new JSizeCommand());
    }

    public static JCommand fromString(String s) throws Exception {
        if (s.length() < 1) {
            throw new Exception("");
        }
        if (s.length() > 4 && s.substring(0, 5).equals("show ")) {
            s = s.replaceFirst(" ", "_");
        }
        String[] tokens = s.split("\\s+", 0);
        if (COMMANDS.containsKey(tokens[0])) {
            JCommand command = COMMANDS.get(tokens[0]);
            if (tokens.length - 1 != command.getArg()) {
                throw new Exception("Unexpected number of arguments: " + command.getArg() + " required");
            }
            command.putArguments(tokens);
            return command;
        } else {
            throw new Exception(tokens[0] + " is unknown command");
        }
    }

    protected int getArg() {
        return arg;
    }
    public abstract void execute(MyTableProvider provider) throws Exception;
    protected void putArguments(String[] args) {
    }
    protected int arg;
}
