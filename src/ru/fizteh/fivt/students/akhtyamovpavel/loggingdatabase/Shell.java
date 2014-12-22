package ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase;

import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.commands.CommandParser;
import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.commands.CommandStorage;
import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.commands.filemap.*;
import ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.commands.table.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class Shell {
    public static final int EMERGENCY_STOP = 1;
    public static final int ERROR_STOP = -1;
    protected Map<String, Command> commandNames;
    private boolean isInteractive;
    private DataBaseTableProvider provider;

    public void startInteractiveMode() {
        isInteractive = true;
        Scanner in = null;
        try {
            in = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                String request = in.nextLine();
                processInteractiveRequest(request);
            }
        } catch (NoSuchElementException exception) {
            System.err.println("Emergency stop of program");
            System.exit(EMERGENCY_STOP);
        }
    }

    public void startPacketMode(String[] arguments) {
        isInteractive = false;
        try {
            for (CommandStorage currentCommand : CommandParser.parseUserRequest(arguments)) {
                processCommand(currentCommand);
            }
        } catch (ParseException pe) {
            printException(pe.getMessage());
        }
    }

    protected void printException(String exceptionText) {
        if (isInteractive) {
            System.out.println(exceptionText);
        } else {
            System.err.println(exceptionText);
            System.exit(ERROR_STOP);
        }
    }

    private void processCommand(CommandStorage command) {
        try {
            if (commandNames.containsKey(command.getCommandName())) {
                String message = commandNames.get(command.getCommandName())
                        .executeCommand(command.getArgumentsList());
                if (message != null) {
                    System.out.println(message);
                }
            } else {
                printException(command.getCommandName() + ": command not found");
            }
        } catch (Exception e) {
            printException(command.getCommandName() + ": " + e.getMessage());
        }
    }

    private void processInteractiveRequest(String request) {
        try {
            for (CommandStorage currentCommand : CommandParser.parseUserRequest(request)) {
                processCommand(currentCommand);
            }
        } catch (ParseException pe) {
            printException(pe.getMessage());
        }
    }

    private void initCommands() {
        commandNames = new HashMap<String, Command>();

        addCommand(new CreateTableCommand(provider));
        addCommand(new DropTableCommand(provider));
        addCommand(new ExitCommand(provider));
        addCommand(new UseCommand(provider, false));
        addCommand(new ShowTablesCommand(provider));
        addCommand(new PutCommand(provider));
        addCommand(new ListCommand(provider));
        addCommand(new GetCommand(provider));
        addCommand(new RemoveCommand(provider));
        addCommand(new RollbackCommand(provider));
        addCommand(new CommitCommand(provider));
    }

    private void addCommand(Command command) {
        commandNames.put(command.getName(), command);
    }

    public void setProvider(DataBaseTableProviderFactory factory, String dir) {
        try {
            provider = new DataBaseTableProvider(dir);
            initCommands();
        } catch (Exception e) {
            System.err.println("Connection error");
            System.exit(ERROR_STOP);
        }
    }

}

