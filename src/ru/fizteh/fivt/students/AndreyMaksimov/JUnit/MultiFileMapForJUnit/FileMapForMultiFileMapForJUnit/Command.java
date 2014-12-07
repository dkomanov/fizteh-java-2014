package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit;

import java.util.HashMap;

public abstract class Command {

    private static final HashMap<String, Command> ARRAY_OF_COMMANDS;

    static {
        ARRAY_OF_COMMANDS = new HashMap<>();
        ARRAY_OF_COMMANDS.put("put", new Put());
        ARRAY_OF_COMMANDS.put("get", new Get());
        ARRAY_OF_COMMANDS.put("list", new List());
        ARRAY_OF_COMMANDS.put("exit", new Exit());
        ARRAY_OF_COMMANDS.put("remove", new Remove());
    }

    public static Command fromString(String needString) throws Exception {
        if (needString.length() < 1) {
            throw new Exception("Empty command");
        }
        String[] tokens = needString.split("\\s+", 0);
        if (ARRAY_OF_COMMANDS.containsKey(tokens[0])) {
            Command command = ARRAY_OF_COMMANDS.get(tokens[0]);
            if (tokens.length - 1 != command.numberOfArguments()) {
                throw new Exception("Unexpected number of arguments " + command.numberOfArguments() + " required");
            } else {
                command.putArguments(tokens);
                return command;
            }
        } else {
            throw new Exception(tokens[0] + " is unknown command");
        }
    }


    public abstract void startNeedInstruction(DataBase base) throws Exception;

    public void putArguments(String[] args) {
    }

    protected abstract int numberOfArguments();
}

