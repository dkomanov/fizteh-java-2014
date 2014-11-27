package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.util.HashMap;

public abstract class CommandFileMap {

    private static final HashMap<String, CommandFileMap> COMMANDS;

    static {
        COMMANDS = new HashMap<>();
        COMMANDS.put("put", new PutCommand());
        COMMANDS.put("get", new GetCommand());
        COMMANDS.put("remove", new RemoveCommand());
        COMMANDS.put("list", new ListCommand());
        COMMANDS.put("exit", new ExitCommandFileMap());
    }

    public abstract void execute(DataBase base) throws Exception;

    public void putArguments(String[] args) {}

    protected abstract int numberOfArguments();
}
