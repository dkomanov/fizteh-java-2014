package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit;

import java.util.HashMap;

public abstract class Command {

    private static final HashMap<String, Command> arrayOfCommands;

    static {
        arrayOfCommands = new HashMap<>();
        arrayOfCommands.put("put", new Put());
        arrayOfCommands.put("get", new Get());
        arrayOfCommands.put("list", new List());
        arrayOfCommands.put("exit", new Exit());
        arrayOfCommands.put("remove", new Remove());
    }

    public static Command fromString(String needString) throws Exception {
        if (needString.length() < 1) {
            throw new Exception("Empty command");
        }
        String[] tokens = needString.split("\\s+", 0);
        if (arrayOfCommands.containsKey(tokens[0])) {
            Command command = arrayOfCommands.get(tokens[0]);
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
