package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands_parser;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.DatabaseCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.commands.*;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.table_manager.commands.*;
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
    private DbManager relatedDbManager;


    /**
     *
     * @param relatedDbManager  DbManager which will be passed to commands' constructors
     */
    public DbCommandsParser(final DbManager relatedDbManager) {
        this.relatedDbManager = relatedDbManager;
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

    private DatabaseCommand parseCommand(final String strCommand) {
        String[] cmdChunks = strCommand.trim().split(DELIMITER_REGEX);
        String[] commandArgs;
        if (cmdChunks.length >= 2 && cmdChunks[0].equals("show") && cmdChunks[1].equals("tables")) {
            commandArgs = Arrays.copyOfRange(cmdChunks, 2, cmdChunks.length);
            return new ShowTablesCommand(commandArgs, relatedDbManager);
        }
        commandArgs = Arrays.copyOfRange(cmdChunks, 1, cmdChunks.length);
        switch (cmdChunks[0]) {
            // TableManagerCommands.
            case "put":
                return new PutCommand(commandArgs, relatedDbManager.getCurrentTable());
            case "get":
                return new GetCommand(commandArgs, relatedDbManager.getCurrentTable());
            case "remove":
                return new RemoveCommand(commandArgs, relatedDbManager.getCurrentTable());
            case "list":
                return new ListCommand(commandArgs, relatedDbManager.getCurrentTable());
            case "size":
                return new SizeCommand(commandArgs, relatedDbManager.getCurrentTable());
            case "commit":
                return new CommitCommand(commandArgs, relatedDbManager.getCurrentTable());
            case "rollback":
                return new RollbackCommand(commandArgs, relatedDbManager.getCurrentTable());
            // DatabaseManagerCommands.
            case "create":
                return new CreateCommand(commandArgs, relatedDbManager);
            case "drop":
                return new DropCommand(commandArgs, relatedDbManager);
            case "use":
                return new UseCommand(commandArgs, relatedDbManager);
            // Exit neither TableManagerCommand nor DbManagerCommand.
            case "exit":
                return new ExitCommand(commandArgs, relatedDbManager);
            default:
                throw new WrongCommandException(strCommand);
        }
    }
}
