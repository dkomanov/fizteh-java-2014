package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.Shell;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.Exceptions.StopExecutionException;

import java.util.HashMap;
import java.util.Scanner;

public class ShellExtended extends Shell<RemoteTableProviderFactory> {
    private HashMap<String, CommandExtended<RemoteTableProviderFactory>> shellCommands;

    private RemoteTableProviderFactory objectForShell;
    public ShellExtended(RemoteTableProviderFactory obj) {
        super(obj);
        shellCommands = new HashMap<>();
        objectForShell = obj;
    }

    public void addCommand(CommandExtended<RemoteTableProviderFactory> newCommand) {
        shellCommands.put(newCommand.toString(), newCommand);
    }

    public boolean interactiveMode() {
        CommandExecutor interpeter = new CommandExecutor();
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
                    CommandExtended<RemoteTableProviderFactory> commandToExecute
                            = shellCommands.get(parsedArguments[0]);
                    if (commandToExecute != null) {
                        try {
                            if (commandToExecute.getNumberOfArguments() != parsedArguments.length
                                    & commandToExecute.getNumberOfArguments() != -1) {
                                System.out.println(commandToExecute.getName() + ": wrong number of arguments");
                                errorOccuried = true;
                            } else if (!commandToExecute.run(objectForShell, parsedArguments)) {
                                errorOccuried = true;
                            }
                        } catch (IllegalStateException e) {
                            System.out.println(e.getMessage());
                            errorOccuried = true;
                        }
                    } else {
                        try {
                            interpeter.run(oneCommand, System.out,
                                    ((RealRemoteTableProviderFactory) objectForShell).getCurrentProvider());
                        } catch (StopExecutionException e) {
                            System.out.println("Server has been stopped!");
                            ((RealRemoteTableProviderFactory) objectForShell).disconnectCurrentProvider();
                            return false;
                        } catch (IllegalStateException e) {
                            errorOccuried = true;
                        }
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
        CommandExecutor interpeter = new CommandExecutor();
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
            CommandExtended<RemoteTableProviderFactory> commandToExecute = shellCommands.get(parsedArguments[0]);
            if (commandToExecute != null) {
                if (commandToExecute.getNumberOfArguments() != parsedArguments.length
                        & commandToExecute.getNumberOfArguments() != -1) {
                    System.out.println(commandToExecute.getName() + " wrong number of arguments");
                    errorOccuried = true;
                } else if (!commandToExecute.run(objectForShell, parsedArguments)) {
                    errorOccuried = true;
                }
            } else {
                try {
                    interpeter.run(oneCommand, System.out,
                            ((RealRemoteTableProviderFactory) objectForShell).getCurrentProvider());
                } catch (StopExecutionException e) {
                    System.out.println("Server has been stopped!");
                    ((RealRemoteTableProviderFactory) objectForShell).disconnectCurrentProvider();
                    return false;
                } catch (IllegalStateException e) {
                    errorOccuried = true;
                }
            }
        }
        return !errorOccuried;
    }
}
