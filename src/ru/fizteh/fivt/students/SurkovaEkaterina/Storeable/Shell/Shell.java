package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Shell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Shell<FilesOperations> {
    private HashMap<String, Command>
            shellCommands = new HashMap<String, Command>();
    private static String invitation = "$ ";
    private String[] arguments;
    private FilesOperations shellOperations;

    public final void setArguments(final String[] args) {
        this.arguments = args;
    }

    public final void setShellCommands(final ArrayList<Command<?>> commands) {
        for (Command<?> command:commands) {
            this.shellCommands.put(command.getCommandName(), command);
        }
    }

    public final void setShellOperations(final FilesOperations operations) {
        this.shellOperations = operations;
    }

    public final void beginExecuting() {
        if ((arguments == null) || (arguments.length == 0)) {
            interactiveMode();
        } else {
            packageMode();
        }
    }

    private void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(invitation);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            String[] commands = CommandsParser.parseCommands(command);
            if (command.length() == 0) {
                System.out.print(invitation);
                continue;
            }
            for (String currentCommand:commands) {
                if (!execute(currentCommand)) {
                    break;
                }
            }
            System.out.print(invitation);
        }
        scanner.close();
    }

    private void packageMode() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s:arguments) {
            stringBuilder.append(s + " ");
        }
        String[] commands = CommandsParser.parseCommands(
                stringBuilder.toString());
        for (String currentCommand:commands) {
            boolean result = execute(currentCommand);
            if (!result) {
                System.exit(-1);
            }
        }

    }

    private boolean execute(final String currentCommand) {
        String commandName = CommandsParser.getCommandName(currentCommand);
        String parameters = CommandsParser.getCommandParameters(currentCommand);
        if (commandName.isEmpty()) {
            System.out.println("Empty command name!");
            return false;
        }
        if (!shellCommands.containsKey(commandName)) {
            System.out.println("Unknown command: \'" + commandName + "\'");
            return false;
        }
        boolean result = true;
        try {
            shellCommands.get(commandName)
                    .executeCommand(parameters, shellOperations);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            result = false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = false;
        }
        return result;
    }
}
