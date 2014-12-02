package ru.fizteh.fivt.students.andreyzakharov.stringfilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.stringfilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.stringfilemap.MultiFileTableProvider;

import java.util.HashMap;
import java.util.Map;

public class CommandRunner {
    private Map<String, Command> commands = new HashMap<>();

    private void registerCommand(Command command) {
        commands.put(command.toString(), command);
    }

    public CommandRunner() {
        registerCommand(new CreateCommand());
        registerCommand(new DropCommand());
        registerCommand(new UseCommand());
        registerCommand(new ShowCommand());

        registerCommand(new PutCommand());
        registerCommand(new GetCommand());
        registerCommand(new SizeCommand());
        registerCommand(new ListCommand());
        registerCommand(new RemoveCommand());

        registerCommand(new CommitCommand());
        registerCommand(new RollbackCommand());

        registerCommand(new ExitCommand());
    }

    public String run(MultiFileTableProvider connector, String commandString) throws CommandInterruptException {
        String[] args = commandString.trim().split("\\s(?![^\\(]*\\))(?![^\\[]*\\])");
        Command command = commands.get(args[0]);
        if (command != null) {
            return command.execute(connector, args);
        } else if (!args[0].trim().isEmpty()) {
            throw new CommandInterruptException(args[0] + ": command not found");
        } else {
            return null;
        }
    }
}
