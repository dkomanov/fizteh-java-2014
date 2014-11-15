package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import java.util.HashMap;

public abstract class JUnitCommand {
    private static final HashMap<String, JUnitCommand> ARRAY_OF_COMMANDS;

    static {
        ARRAY_OF_COMMANDS = new HashMap<>();
        ARRAY_OF_COMMANDS.put("create", new JUnitCreate());
        ARRAY_OF_COMMANDS.put("drop", new JUnitDrop());
        ARRAY_OF_COMMANDS.put("use", new JUnitUse());
        ARRAY_OF_COMMANDS.put("show_tables", new JUnitShow());
        ARRAY_OF_COMMANDS.put("put", new JUnitPut());
        ARRAY_OF_COMMANDS.put("get", new JUnitGet());
        ARRAY_OF_COMMANDS.put("remove", new JUnitRemove());
        ARRAY_OF_COMMANDS.put("list", new JUnitList());
        ARRAY_OF_COMMANDS.put("exit", new JUnitExit());
        ARRAY_OF_COMMANDS.put("commit", new Commit());
        ARRAY_OF_COMMANDS.put("rollback", new RollBack());
        ARRAY_OF_COMMANDS.put("size", new Size());
    }

    public static JUnitCommand fromString(String needString) throws Exception {
        if (needString.length() < 1) {
            throw new Exception("Empty command");
        }
        if (needString.length() > 4 && needString.substring(0, 5).equals("show ")) {
            needString = needString.replaceFirst(" ", "_");
        }
        String[] tokens = needString.split("\\s+", 0);
        if (ARRAY_OF_COMMANDS.containsKey(tokens[0])) {
            JUnitCommand command = ARRAY_OF_COMMANDS.get(tokens[0]);
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
    protected void putArguments(String[] args) {
    }
    protected abstract int numberOfArguments();
}
