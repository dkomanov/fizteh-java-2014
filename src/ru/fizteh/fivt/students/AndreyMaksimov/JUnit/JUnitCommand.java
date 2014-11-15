package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import java.util.HashMap;

public abstract class JUnitCommand {
    private static final HashMap<String, JUnitCommand> arrayOfCommands;

    static {
        arrayOfCommands = new HashMap<>();
        arrayOfCommands.put("create", new JUnitCreate());
        arrayOfCommands.put("drop", new JUnitDrop());
        arrayOfCommands.put("use", new JUnitUse());
        arrayOfCommands.put("show_tables", new JUnitShow());
        arrayOfCommands.put("put", new JUnitPut());
        arrayOfCommands.put("get", new JUnitGet());
        arrayOfCommands.put("remove", new JUnitRemove());
        arrayOfCommands.put("list", new JUnitList());
        arrayOfCommands.put("exit", new JUnitExit());
        arrayOfCommands.put("commit", new Commit());
        arrayOfCommands.put("rollback", new RollBack());
        arrayOfCommands.put("size", new Size());
    }

    public static JUnitCommand fromString(String needString) throws Exception {
        if (needString.length() < 1) {
            throw new Exception("Empty command");
        }
        if (needString.length() > 4 && needString.substring(0, 5).equals("show ")) {
            needString = needString.replaceFirst(" ", "_");
        }
        String[] tokens = needString.split("\\s+", 0);
        if (arrayOfCommands.containsKey(tokens[0])) {
            JUnitCommand command = arrayOfCommands.get(tokens[0]);
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