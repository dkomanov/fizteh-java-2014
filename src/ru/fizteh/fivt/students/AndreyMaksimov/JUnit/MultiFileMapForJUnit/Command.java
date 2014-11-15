package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

import java.util.HashMap;


public abstract class Command {

    private static final HashMap<String, Command> arrayOfCommands;

    static {
        arrayOfCommands = new HashMap<>();
        arrayOfCommands.put("use", new Use());
        arrayOfCommands.put("drop", new Drop());
        arrayOfCommands.put("get", new GetMulti());
        arrayOfCommands.put("put", new PutMulti());
        arrayOfCommands.put("create", new Create());
        arrayOfCommands.put("exit", new ExitMulti());
        arrayOfCommands.put("list", new ListMulti());
        arrayOfCommands.put("remove", new RemoveMulti());
        arrayOfCommands.put("show_tables", new ShowTables());
    }

    public static Command fromString(String needString) throws Exception {
        return vocabularyGetter(needString, arrayOfCommands);
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