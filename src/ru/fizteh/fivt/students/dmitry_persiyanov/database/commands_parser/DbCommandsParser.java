package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands_parser;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.commands.*;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.commands.*;
import ru.fizteh.fivt.students.dmitry_persiyanov.interpreter.InterpreterCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.interpreter.InterpreterCommandsParser;
import ru.fizteh.fivt.students.dmitry_persiyanov.interpreter.exceptions.WrongCommandException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public final class DbCommandsParser implements InterpreterCommandsParser {
    public static final String COMMANDS_SEPARATOR = ";";
    public static final String DELIMITER_REGEX = "\\s+";
    private DbTableProvider relatedDbTableProvider;


    /**
     *
     * @param relatedDbTableProvider  DbManager which will be passed to commands' constructors
     */
    public DbCommandsParser(final DbTableProvider relatedDbTableProvider) {
        this.relatedDbTableProvider = relatedDbTableProvider;
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
        String[] cmdChunks = strCommand.trim().split(DELIMITER_REGEX);
        String[] commandArgs;
        if (cmdChunks.length >= 2 && cmdChunks[0].equals("show") && cmdChunks[1].equals("tables")) {
            commandArgs = Arrays.copyOfRange(cmdChunks, 2, cmdChunks.length);
            return new ShowTablesCommand(commandArgs, relatedDbTableProvider);
        }
        commandArgs = Arrays.copyOfRange(cmdChunks, 1, cmdChunks.length);
        switch (cmdChunks[0]) {
            // TableManagerCommands.
            case "put":
                return new PutCommand(commandArgs, relatedDbTableProvider.getCurrentTable());
            case "get":
                return new GetCommand(commandArgs, relatedDbTableProvider.getCurrentTable());
            case "remove":
                return new RemoveCommand(commandArgs, relatedDbTableProvider.getCurrentTable());
            case "list":
                return new ListCommand(commandArgs, relatedDbTableProvider.getCurrentTable());
            case "size":
                return new SizeCommand(commandArgs, relatedDbTableProvider.getCurrentTable());
            case "commit":
                return new CommitCommand(commandArgs, relatedDbTableProvider.getCurrentTable());
            case "rollback":
                return new RollbackCommand(commandArgs, relatedDbTableProvider.getCurrentTable());
            // DatabaseManagerCommands.
            case "create":
                return new CreateCommand(commandArgs, relatedDbTableProvider);
            case "drop":
                return new DropCommand(commandArgs, relatedDbTableProvider);
            case "use":
                return new UseCommand(commandArgs, relatedDbTableProvider);
            // Exit neither TableManagerCommand nor DbManagerCommand.
            case "exit":
                return new ExitCommand(commandArgs, relatedDbTableProvider);
            default:
                throw new WrongCommandException(strCommand);
        }
    }
}
