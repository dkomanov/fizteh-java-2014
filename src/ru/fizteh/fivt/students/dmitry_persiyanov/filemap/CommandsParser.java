package ru.fizteh.fivt.students.dmitry_persiyanov.filemap;

import ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands.*;

public final class CommandsParser {
    public static Command[] parse(final String[] args) {
        StringBuilder buf = new StringBuilder();
        for (String arg : args) {
            buf.append(arg);
            buf.append(" ");
        }
        String[] strCommands = buf.toString().trim().split(";");
        Command[] commands = new Command[strCommands.length];
        for (int i = 0; i < strCommands.length; ++i) {
            commands[i] = parseCommand(strCommands[i]);
        }
        return commands;
    }

    public static Command parse(final String arg) {
        String[] args = new String[1];
        args[0] = arg;
        Command[] cmds = parse(args);
        return cmds[0];
    }

    private static Command parseCommand(final String strCommand) {
        String[] args = strCommand.trim().split("\\s+");
        switch (args[0]) {
            case "put":
                return new PutCommand(args);
            case "get":
                return new GetCommand(args);
            case "remove":
                return new RemoveCommand(args);
            case "list":
                return new ListCommand(args);
            case "exit":
                return new ExitCommand(args);
            default:
                throw new IllegalArgumentException("wrong command: " + strCommand);
        }
    }
}
