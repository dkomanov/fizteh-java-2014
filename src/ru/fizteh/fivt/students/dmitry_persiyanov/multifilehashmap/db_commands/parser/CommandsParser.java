package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.parser;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.*;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.db_manager_commands.*;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.table_manager_commands.GetCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.table_manager_commands.ListCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.table_manager_commands.PutCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.table_manager_commands.RemoveCommand;

import java.util.Arrays;


public final class CommandsParser {
    public static DbCommand[] parse(final String[] args) {
        String[] strCommands = String.join(" ", args).trim().split(";");
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
        String[] argsForCmd;
        if (args.length >= 2 && args[0].equals("show") && args[1].equals("tables")) {
            argsForCmd = Arrays.copyOfRange(args, 2, args.length);
            return new ShowTablesCommand(argsForCmd);
        }
        argsForCmd = Arrays.copyOfRange(args, 1, args.length);
        switch (args[0]) {
            // TableManagerCommands.
            case "put":
                return new PutCommand(argsForCmd);
            case "get":
                return new GetCommand(argsForCmd);
            case "remove":
                return new RemoveCommand(argsForCmd);
            case "list":
                return new ListCommand(argsForCmd);
            // DbManagerCommands.
            case "create":
                return new CreateCommand(argsForCmd);
            case "drop":
                return new DropCommand(argsForCmd);
            case "use":
                return new UseCommand(argsForCmd);
            // Exit neither TableManagerCommand nor DbManagerCommand.
            case "exit":
                return new ExitCommand(argsForCmd);
            default:
                throw new IllegalArgumentException("wrong command: " + strCommand);
        }
    }
}
