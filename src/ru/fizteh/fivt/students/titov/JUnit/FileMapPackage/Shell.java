package ru.fizteh.fivt.students.titov.JUnit.FileMapPackage;

import java.util.HashMap;
import java.util.Scanner;

public class Shell<T> {
    private static final String SPECIAL_CHARACTER = "$ ";
    private HashMap<String, Command<T>> shellCommands;

    private T objectForShell;
    public Shell(T obj) {
        shellCommands = new HashMap<>();
        objectForShell = obj;
    }

    public void addCommand(Command<T> newCommand) {
        shellCommands.put(newCommand.toString(), newCommand);
    }

    public boolean doCommands(String[] parsedCommands, boolean errorOccuried) {
        String[] parsedArguments;
        for (String oneCommand : parsedCommands) {
            parsedArguments = oneCommand.trim().split("\\s+");
            if (parsedArguments.length == 0 || parsedArguments[0].equals("")) {
                continue;
            }
            Command<T> commandToExecute = shellCommands.get(parsedArguments[0]);
            if (commandToExecute != null) {
                if (commandToExecute.numberOfArguments != parsedArguments.length) {
                    System.err.println(commandToExecute.name + " wrong number of arguments");
                    errorOccuried = true;
                } else if (!commandToExecute.run(objectForShell, parsedArguments)) {
                    errorOccuried = true;
                }
            } else {
                System.err.println(parsedArguments[0] + ": command not found");
                errorOccuried = true;
            }
        }
        return !errorOccuried;
    }

    public boolean interactiveMode() {
        System.out.print(SPECIAL_CHARACTER);
        boolean ended = false;
        boolean errorOccuried = false;

        try (Scanner inStream = new Scanner(System.in)) {
            String[] parsedCommands;
            while (!ended) {
                if (inStream.hasNextLine()) {
                    parsedCommands = inStream.nextLine().split(";|\n");
                } else {
                    break;
                }
                errorOccuried = doCommands(parsedCommands, errorOccuried);
                if (!ended) {
                    System.out.print(SPECIAL_CHARACTER);
                }
            }
        }
        return !errorOccuried;
    }

    public boolean batchMode(final String[] arguments) {

        String[] parsedCommands;
        String commandLine = arguments[0];
        boolean errorOccuried = false;

        for (int i = 1; i < arguments.length; ++i) {
            commandLine = commandLine + " " + arguments[i];
        }

        parsedCommands = commandLine.split(";|\n");
        if (parsedCommands.length == 0) {
            return true;
        }
        return !doCommands(parsedCommands, errorOccuried);
    }
}
