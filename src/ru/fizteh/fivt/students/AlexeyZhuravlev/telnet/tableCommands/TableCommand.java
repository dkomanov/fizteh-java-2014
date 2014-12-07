package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ShellTableProvider;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.UnknownCommandException;

import java.io.PrintStream;
import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */
public abstract class TableCommand {
    private static final HashMap<String, TableCommand> COMMANDS;

    static {
        COMMANDS = new HashMap<>();
        COMMANDS.put("create", new CreateTableCommand());
        COMMANDS.put("drop", new DropTableCommand());
        COMMANDS.put("use", new UseTableCommand());
        COMMANDS.put("show_tables", new ShowTablesTableCommand());
        COMMANDS.put("put", new PutTableCommand());
        COMMANDS.put("get", new GetTableCommand());
        COMMANDS.put("remove", new RemoveTableCommand());
        COMMANDS.put("list", new ListTableCommand());
        COMMANDS.put("exit", new ExitTableCommand());
        COMMANDS.put("commit", new CommitTableCommand());
        COMMANDS.put("rollback", new RollBackTableCommand());
        COMMANDS.put("size", new SizeTableCommand());
        COMMANDS.put("describe", new DescribeCommand());
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

    public static TableCommand fromString(String s) throws Exception {
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
            TableCommand command = COMMANDS.get(tokens[0]);
            if (tokens.length - 1 != command.numberOfArguments()) {
                throw new Exception("Unexpected number of arguments: " + command.numberOfArguments() + " required");
            }
            command.putArguments(tokens);
            return command;
        } else {
            throw new UnknownCommandException();
        }
    }

    public abstract void execute(ShellTableProvider base, PrintStream stream) throws Exception;
    protected void putArguments(String[] args) throws Exception {
    }
    protected abstract int numberOfArguments();
}
