package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider.CommandTableProviderExtended;

import java.util.HashMap;
import java.util.Scanner;

public class InnerShell {
    private HashMap<String, CommandTableProviderExtended> shellCommands;
    private RealRemoteTableProviderFactory objectForShell;

    public InnerShell(RealRemoteTableProviderFactory obj) {
        objectForShell = obj;
        shellCommands = new HashMap<>();
    }

    public boolean interactiveMode() {
        System.out.print("$ ");
        boolean ended = false;
        boolean errorOccuried = false;

        try (Scanner inStream = new Scanner(System.in)) {
            String[] parsedCommands;
            String[] parsedArguments;
            while (!ended) {
                if (inStream.hasNextLine()) {
                    parsedCommands = inStream.nextLine().split(";|\n");
                } else {
                    break;
                }
                for (String oneCommand : parsedCommands) {
                    parsedArguments = oneCommand.trim().split("\\s+");
                    if (parsedArguments[0].equals("put")) {
                        if (oneCommand.contains("<")) {
                            String valueForPut = oneCommand.trim().substring(oneCommand.indexOf('<'));
                            parsedArguments[2] = valueForPut;
                        }
                    }
                    if (parsedArguments.length == 0 || parsedArguments[0].equals("")) {
                        continue;
                    }
                    if (parsedArguments[0].equals("exit")) {
                        ended = true;
                        break;
                    }
                    CommandTableProviderExtended commandToExecute = shellCommands.get(parsedArguments[0]);
                    if (commandToExecute != null) {
                        try {
                            if (commandToExecute.getNumberOfArguments() != parsedArguments.length
                                    & commandToExecute.getNumberOfArguments() != -1) {
                                System.out.println(commandToExecute.getName() + ": wrong number of arguments");
                                errorOccuried = true;
                            } else if (!commandToExecute.run(objectForShell.getCurrentProvider(), parsedArguments, System.out)) {
                                errorOccuried = true;
                            }
                        } catch (IllegalStateException e) {
                            System.out.println(e.getMessage());
                            errorOccuried = true;
                        }
                    } else {
                        System.out.println(parsedArguments[0] + ": command not found");
                        errorOccuried = true;
                    }
                }
                if (!ended) {
                    System.out.print("$ ");
                }
            }
        }
        return !errorOccuried;
    }

    public boolean packetMode(final String[] arguments) {

        String[] parsedCommands;
        String[] parsedArguments;
        String commandLine = arguments[0];
        boolean errorOccuried = false;

        for (int i = 1; i < arguments.length; ++i) {
            commandLine = commandLine + " " + arguments[i];
        }

        parsedCommands = commandLine.split(";|\n");
        if (parsedCommands.length == 0) {
            return true;
        }
        for (String oneCommand : parsedCommands) {
            parsedArguments = oneCommand.trim().split("\\s+");
            if (parsedArguments.length == 0 || parsedArguments[0].equals("")) {
                continue;
            }
            if (parsedArguments[0].equals("exit")) {
                return !errorOccuried;
            }
            CommandTableProviderExtended commandToExecute = shellCommands.get(parsedArguments[0]);
            if (commandToExecute != null) {
                if (commandToExecute.getNumberOfArguments() != parsedArguments.length
                        & commandToExecute.getNumberOfArguments() != -1) {
                    System.out.println(commandToExecute.getName() + " wrong number of arguments");
                    errorOccuried = true;
                } else if (!commandToExecute.run(objectForShell.getCurrentProvider(), parsedArguments, System.out)) {
                    errorOccuried = true;
                }
            } else {
                System.out.println(parsedArguments[0] + ": command not found");
                errorOccuried = true;
            }
        }
        return !errorOccuried;
    }
}
