package ru.fizteh.fivt.students.akhtyamovpavel.shell;

import ru.fizteh.fivt.students.akhtyamovpavel.shell.commands.Command;
import ru.fizteh.fivt.students.akhtyamovpavel.shell.commands.CommandParser;
import ru.fizteh.fivt.students.akhtyamovpavel.shell.commands.CommandStorage;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public abstract class AbstractShell {
    protected Map<String, Command> commandNames;
    private boolean isInteractive;
    private boolean hasPacketErrors;

    public static final int EMERGENCY_STOP = 1;
    public static final int ERROR_STOP = -1;


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
        for (CommandStorage currentCommand : CommandParser.parseUserRequest(arguments)) {
            processCommand(currentCommand);
        }
    }

    private void printException(String exceptionText) {
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
                commandNames.get(command.getCommandName()).executeCommand(command.getArgumentsList());
            } else {
                printException(command.getCommandName() + ": command not found");
            }
        } catch (Exception e) {
            hasPacketErrors = true;
            printException(e.getMessage());
        }

    }

    private void processInteractiveRequest(String request) {
        for (CommandStorage currentCommand : CommandParser.parseUserRequest(request)) {
            processCommand(currentCommand);
        }
    }
}
