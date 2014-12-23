package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private static Map<String, Command> commands;

    static {
        commands = new HashMap<String, Command>();
        commands.put("put", new PutCommand());
        commands.put("get", new GetCommand());
        commands.put("remove", new RemoveCommand());
        commands.put("list", new ListCommand());
        commands.put("create", new CreateCommand());
        commands.put("drop", new DropCommand());
        commands.put("use", new UseCommand());
        commands.put("show", new ShowTablesCommand());
        commands.put("size", new SizeCommand());
        commands.put("commit", new CommitCommand());
        commands.put("rollback", new RollbackCommand());
        commands.put("exit", new ExitCommand());
    }

    public static Command parseCommand(ParallelTableProvider base, String[] args) throws ParallelException {
        if (!commands.containsKey(args[0])) {
            throw new ParallelException("Unknown command");
        }
        Command command = commands.get(args[0]);
        command.putArguments(base, Arrays.copyOfRange(args, 1, args.length));
        return command;
    }
}
