package DbCommands;

import TableManager.TableManagerCommands.ExitCommand;
import TableManager.TableManagerCommands.GetCommand;
import TableManager.TableManagerCommands.ListCommand;
import TableManager.TableManagerCommands.PutCommand;
import TableManager.TableManagerCommands.RemoveCommand;

public final class CommandsParser {
    public static DbCommand[] parse(final String[] args) {
        StringBuilder buf = new StringBuilder();
        for (String arg : args) {
            buf.append(arg);
            buf.append(" ");
        }
        String[] strCommands = buf.toString().trim().split(";");
        DbCommand[] commands = new DbCommand[strCommands.length];
        for (int i = 0; i < strCommands.length; ++i) {
            commands[i] = parseCommand(strCommands[i]);
        }
        return commands;
    }

    public static DbCommand parse(final String arg) {
        String[] args = new String[1];
        args[0] = arg;
        DbCommand[] cmds = parse(args);
        return cmds[0];
    }

    private static DbCommand parseCommand(final String strCommand) {
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
