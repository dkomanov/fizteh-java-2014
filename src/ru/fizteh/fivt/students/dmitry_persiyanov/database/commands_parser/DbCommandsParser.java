package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands_parser;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.table_commands.*;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.tableprovider_commands.CreateCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.tableprovider_commands.DropCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.tableprovider_commands.ShowTablesCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.tableprovider_commands.UseCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.interpreter.InterpreterCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.interpreter.InterpreterCommandsParser;
import ru.fizteh.fivt.students.dmitry_persiyanov.interpreter.exceptions.WrongCommandException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public final class DbCommandsParser implements InterpreterCommandsParser {
    public static final String COMMANDS_SEPARATOR = ";";
    public static final String DELIMITER_REGEXP = "\\s+";
    private DbTableProvider tableProvider;


    /**
     *
     * @param tableProvider  DbManager which will be passed to commands' constructors
     */
    public DbCommandsParser(final DbTableProvider tableProvider) {
        this.tableProvider = tableProvider;
    }

    @Override
    public List<InterpreterCommand> parseAllInput(final Scanner input) {
        String inputLine = input.nextLine();
        String[] strCommands = inputLine.trim().split(COMMANDS_SEPARATOR);
        List<InterpreterCommand> commands = new LinkedList<>();
        for (String strCommand : strCommands) {
            commands.add(parseCommand(strCommand));
        }
        return commands;
    }

    @Override
    public InterpreterCommand parseOneCommand(final Scanner input) {
        return parseAllInput(input).get(0);
    }

    private DbCommand parseCommand(final String strCommand) {
        String[] cmdChunks = strCommand.trim().split(DELIMITER_REGEXP);
        String[] commandArgs;
        if (cmdChunks.length >= 2 && cmdChunks[0].equals("show") && cmdChunks[1].equals("tables")) {
            commandArgs = Arrays.copyOfRange(cmdChunks, 2, cmdChunks.length);
            return new ShowTablesCommand(commandArgs, tableProvider);
        }
        commandArgs = Arrays.copyOfRange(cmdChunks, 1, cmdChunks.length);
        switch (cmdChunks[0]) {
            // TableManagerCommands.
            case "put":
                return new PutCommand(commandArgs, tableProvider, tableProvider.getCurrentTable());
            case "get":
                return new GetCommand(commandArgs, tableProvider, tableProvider.getCurrentTable());
            case "remove":
                return new RemoveCommand(commandArgs, tableProvider, tableProvider.getCurrentTable());
            case "list":
                return new ListCommand(commandArgs, tableProvider, tableProvider.getCurrentTable());
            case "size":
                return new SizeCommand(commandArgs, tableProvider, tableProvider.getCurrentTable());
            case "commit":
                return new CommitCommand(commandArgs, tableProvider, tableProvider.getCurrentTable());
            case "rollback":
                return new RollbackCommand(commandArgs, tableProvider, tableProvider.getCurrentTable());
            case "exit":
                return new ExitCommand(commandArgs, tableProvider, tableProvider.getCurrentTable());
            // DatabaseManagerCommands.
            case "create":
                return new CreateCommand(commandArgs, tableProvider);
            case "drop":
                return new DropCommand(commandArgs, tableProvider);
            case "use":
                return new UseCommand(commandArgs, tableProvider);
            default:
                throw new WrongCommandException(strCommand);
        }
    }
}
