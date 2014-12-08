package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.CommandParser;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.CommandStorage;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.client.ConnectCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.client.DestinationCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.client.DisconnectCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.filemap.*;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.server.ListUsersCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.server.StartServerCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.server.StopServerCommand;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands.table.*;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote.RemoteDataBaseTableProviderFactory;

import java.text.ParseException;
import java.util.*;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class Shell {
    public static final int EMERGENCY_STOP = 1;
    public static final int ERROR_STOP = -1;
    protected Map<String, Command> commandNames;
    private boolean isInteractive;
    private RemoteDataBaseTableProvider provider;

    public void startInteractiveMode() {
        isInteractive = true;
        Scanner in = null;
        try {
            in = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                String request = in.nextLine();
                processInteractiveRequest(request, false);
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
                processCommand(currentCommand, false);
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

    private String processCommand(CommandStorage command, boolean hosted) {
        try {
            if (commandNames.containsKey(command.getCommandName())) {
                String message = commandNames.get(command.getCommandName())
                        .executeCommand(command.getArgumentsList());
                if (message != null) {
                    if (!hosted) {
                        System.out.println(message);
                    }
                    return message;
                } else {
                    return "";
                }

            } else {
                if (!hosted) {
                    printException(command.getCommandName() + ": command not found");
                }
                return command.getCommandName() + ": command not found";
            }
        } catch (Exception e) {
            if (!hosted) {
                printException(command.getCommandName() + ": " + e.getMessage());
            }
            return command.getCommandName() + ": " + e.getMessage();
        }
    }

    public ArrayList<String> processInteractiveRequest(String request, boolean hosted) {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (CommandStorage currentCommand : CommandParser.parseUserRequest(request)) {
                result.add(processCommand(currentCommand, hosted));
            }
        } catch (ParseException pe) {
            printException(pe.getMessage());
            result.add(pe.getMessage());
        }
        return result;
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
        addCommand(new StartServerCommand(provider));
        addCommand(new ConnectCommand(provider));
        addCommand(new StopServerCommand(provider));
        addCommand(new ListUsersCommand(provider));
        addCommand(new DisconnectCommand(provider));
        addCommand(new DestinationCommand(provider));
    }

    private void addCommand(Command command) {
        commandNames.put(command.getName(), command);
    }

    public void setProvider(RemoteDataBaseTableProviderFactory factory, String dir) {
        try {
            provider = new RemoteDataBaseTableProvider(new DataBaseTableProvider(dir));
            initCommands();
        } catch (Exception e) {
            System.err.println("Connection error");
            System.exit(ERROR_STOP);
        }
    }


    public void setProvider(RemoteDataBaseTableProvider localProvider) {
        provider = localProvider;
        initCommands();
    }
}

