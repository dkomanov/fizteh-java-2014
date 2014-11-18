package ru.fizteh.fivt.students.dnovikov.junit.Interpreter;

import ru.fizteh.fivt.students.dnovikov.junit.DataBaseProvider;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.LoadOrSaveException;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.StopInterpreterException;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.WrongNumberOfArgumentsException;

import java.io.*;
import java.util.*;

public class Interpreter {

    private final Map<String, Command> commands;
    private final DataBaseProvider dbConnector;
    private final InputStream in;
    private final PrintStream out;
    private final PrintStream err;
    private boolean isBatch;

    public Interpreter(DataBaseProvider dbConnector, InputStream in, PrintStream out, PrintStream err, Command[] cmds) {
        if (in == null || out == null || err == null) {
            throw new IllegalArgumentException("InputStream or OutputStream is null");
        }
        this.in = in;
        this.out = out;
        this.err = err;
        this.dbConnector = dbConnector;
        this.commands = new HashMap<>();
        for (Command command : cmds) {
            this.commands.put(command.getName(), command);
        }
    }

    private static String[] toArray(List<String> list) {
        String[] result = new String[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    private static String[][] toArray2D(List<String[]> list) {
        String[][] result = new String[list.size()][];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public void run(String[] args) {
        try {
            if (args.length == 0) {
                isBatch = false;
                interactiveMode();
            } else {
                isBatch = true;
                batchMode(args);
            }
        } catch (StopInterpreterException e) {
            // Stop the interpreter.
        }
    }

    private void batchMode(String[] args) throws StopInterpreterException {
        try {
            List<String> currentCommand = new ArrayList<>();
            List<String[]> commands = new ArrayList<>();
            for (int i = 0; i < args.length; i++) {
                String[] tokens = args[i].split(";", -1);
                for (int j = 0; j < tokens.length; j++) {
                    if (j > 0) {
                        commands.add(toArray(currentCommand));
                        currentCommand = new ArrayList<>();
                    }
                    if (!tokens[j].equals("")) {
                        currentCommand.add(tokens[j]);
                    }
                }
            }
            if (currentCommand.size() > 0) {
                commands.add(toArray(currentCommand));
            }
            invokeCommands(toArray2D(commands));
        } catch (IOException | LoadOrSaveException | WrongNumberOfArgumentsException e) {
            err.println(e.getMessage());
            try {
                if (dbConnector.getCurrentTable() != null) {
                    int unsavedChanges = dbConnector.getCurrentTable().getNumberOfChanges();
                    if (unsavedChanges > 0) {
                        out.println(unsavedChanges + " unsaved changes");
                    } else {
                        dbConnector.saveTable();
                    }
                } else {
                    dbConnector.saveTable();
                }
            } catch (LoadOrSaveException exception) {
                err.println(exception.getMessage());
            }
            System.exit(1);
        }
    }

    private void interactiveMode() throws StopInterpreterException {
        out.print("$ ");
        try (Scanner scanner = new Scanner(in)) {
            while (true) {
                try {
                    String str = scanner.nextLine();
                    parseInvokeInteractiveLine(str);
                } catch (IOException e) {
                    err.println(e.getMessage());
                } catch (LoadOrSaveException | WrongNumberOfArgumentsException e) {
                    err.println(e.getMessage());
                } catch (NoSuchElementException e) {
                    err.println(e.getMessage());
                    break;
                }
                out.print("$ ");
            }
        }
    }

    private void parseInvokeInteractiveLine(String line) throws StopInterpreterException,
            WrongNumberOfArgumentsException, IOException {
        String[] commandsWithArgs = line.trim().split(";");
        String[][] parsedCommands = new String[commandsWithArgs.length][];
        for (int i = 0; i < commandsWithArgs.length; i++) {
            parsedCommands[i] = commandsWithArgs[i].trim().split("\\s+");
        }
        invokeCommands(parsedCommands);
    }

    private void invokeCommands(String[][] parsedCommands) throws StopInterpreterException, IOException {
        for (String[] arguments : parsedCommands) {
            String commandName = arguments[0];
            if (commandName.equals("") || commandName.equals(";")) {
                return;
            }
            Command cmd = commands.get(commandName);
            if (cmd == null) {
                if (commandName.equals("exit")) {
                    throw new StopInterpreterException();
                } else {
                    throw new IOException(commandName + ": no such command");
                }
            }
            String[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
            cmd.invoke(dbConnector, args);
        }
    }

    public boolean isBatch() {
        return isBatch;
    }
}
