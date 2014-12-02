package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

import java.util.HashMap;


public abstract class Command {

    private static final HashMap<String, Command> ARRAY_OF_COMMANDS;

    static {
        ARRAY_OF_COMMANDS = new HashMap<>();
        ARRAY_OF_COMMANDS.put("use", new Use());
        ARRAY_OF_COMMANDS.put("drop", new Drop());
        ARRAY_OF_COMMANDS.put("get", new GetMulti());
        ARRAY_OF_COMMANDS.put("put", new PutMulti());
        ARRAY_OF_COMMANDS.put("create", new Create());
        ARRAY_OF_COMMANDS.put("exit", new ExitMulti());
        ARRAY_OF_COMMANDS.put("list", new ListMulti());
        ARRAY_OF_COMMANDS.put("remove", new RemoveMulti());
        ARRAY_OF_COMMANDS.put("show_tables", new ShowTables());
    }

    public static Command fromString(String needString) throws Exception {
        return vocabularyGetter(needString, ARRAY_OF_COMMANDS);
    }

    public static Command vocabularyGetter(String needString, HashMap<String, Command> commands) throws Exception {
        if (needString.length() < 1) {
            throw new Exception("Empty command");
        }
        if (needString.length() > 4 && needString.substring(0, 5).equals("show ")) {
            needString = needString.replaceFirst(" ", "_");
        }
        String[] tokens = needString.split("\\s+", 0);
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


    public abstract void startNeedInstruction(DataBaseDir base) throws Exception;
    protected void putArguments(String[] args) {
    }
    protected abstract int numberOfArguments();
    public void executeOnTable(Table table) throws Exception {
    }
}

