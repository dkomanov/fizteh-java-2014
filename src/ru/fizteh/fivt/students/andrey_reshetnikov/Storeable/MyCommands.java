package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable;

import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Commands.*;

import java.util.HashMap;

public class MyCommands {
    public static final HashMap<String, Command> COMMANDS;

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
}
