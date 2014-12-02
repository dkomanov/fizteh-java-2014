package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.commands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.MyTableProvider;

import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */
public abstract class JCommand {
    private static final HashMap<String, JCommand> COMMANDS;

    static {
        COMMANDS = new HashMap<>();
        COMMANDS.put("create", new CreateCommand());
        COMMANDS.put("drop", new DropCommand());
        COMMANDS.put("use", new JUseCommand());
        COMMANDS.put("show_tables", new JShowTablesCommand());
        COMMANDS.put("put", new PutCommand());
        COMMANDS.put("get", new GetCommand());
        COMMANDS.put("remove", new RemoveCommand());
        COMMANDS.put("list", new ListCommand());
        COMMANDS.put("exit", new ExitCommand());
        COMMANDS.put("commit", new CommitCommand());
        COMMANDS.put("rollback", new RollBackCommand());
        COMMANDS.put("size", new SizeCommand());
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
            if (tokens.length - 1 != command.numberOfArguments()) {
                throw new Exception("Unexpected number of arguments: " + command.numberOfArguments() + " required");
            }
            command.putArguments(tokens);
            return command;
        } else {
            throw new Exception(tokens[0] + " is unknown command");
        }
    }

    public abstract void execute(MyTableProvider provider) throws Exception;
    protected void putArguments(String[] args) {
    }
    protected abstract int numberOfArguments();
}
