package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable;

import java.util.HashMap;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Commands.*;

public abstract class Command {
    private static final HashMap<String, Command> COMMANDS;

    static {
        COMMANDS = new HashMap<>();
        COMMANDS.put("create", new CreateCommand());
        COMMANDS.put("drop", new DropCommand());
        COMMANDS.put("use", new UseCommand());
        COMMANDS.put("show_tables", new ShowTablesCommand());
        COMMANDS.put("put", new PutCommand());
        COMMANDS.put("get", new GetCommand());
        COMMANDS.put("remove", new RemoveCommand());
        COMMANDS.put("list", new ListCommand());
        COMMANDS.put("exit", new ExitCommand());
        COMMANDS.put("commit", new CommitCommand());
        COMMANDS.put("rollback", new RollBackCommand());
        COMMANDS.put("size", new SizeCommand());
    }

    private static String replaceInnerSpaces(String s, char occur) throws Exception {
        boolean flag = false;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == occur) {
                flag = true;
            }
            if (flag && s.charAt(i) == ' ') {
                result.append('`');
            } else {
                result.append(s.charAt(i));
            }
        }
        if (!flag) {
            return null;
        }
        return result.toString();
    }

    public static Command fromString(String s) throws Exception {
        if (s.length() < 1) {
            throw new Exception("");
        }
        if (s.length() > 4 && s.substring(0, 5).equals("show ")) {
            s = s.replaceFirst(" ", "_");
        }
        if (s.length() > 6 && s.substring(0, 7).equals("create ")) {
            s = replaceInnerSpaces(s, '(');
            if (s == null) {
                throw new Exception("wrong type (create statement must have types in ())");
            }
        }
        if (s.length() > 3 && s.substring(0, 4).equals("put ")) {
            s = replaceInnerSpaces(s, '<');
            if (s == null) {
                throw new Exception("wrong type (value must be xml-serialized)");
            }
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

    public abstract void execute(MyStoreableTableProvider base) throws Exception;
    protected void putArguments(String[] args) throws Exception {
    }
    protected abstract int numberOfArguments();
}
