package ru.fizteh.fivt.students.dnovikov.junit.Interpreter;

import ru.fizteh.fivt.students.dnovikov.junit.DataBaseProvider;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.LoadOrSaveException;
import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.WrongNumberOfArgumentsException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

public class Interpreter {

    private final Map<String, Command> commands;
    private final DataBaseProvider dbConnector;
    private InputStream in;
    private PrintStream out;

    public Interpreter(DataBaseProvider dbConnector, InputStream in, PrintStream out, Command[] commands) {
        if (in == null || out == null) {
            throw new IllegalArgumentException("InputStream or OutputStream is null");
        }
        this.in = in;
        this.out = out;
        this.dbConnector = dbConnector;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public void run(String[] args) throws IOException, LoadOrSaveException {
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode(args);
        }
    }

    private void batchMode(String[] args) {
        try {
            invokeLine(String.join(" ", args));
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
            dbConnector.saveTable();
        } catch (IOException | LoadOrSaveException e) {
            System.err.println(e.getMessage());
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
            } catch (IOException | LoadOrSaveException exception) {
                System.err.println(exception.getMessage());
            }
            System.exit(1);
        }
    }

    private void interactiveMode() {
        System.out.print("$ ");
        try (Scanner scanner = new Scanner(in)) {
            while (true) {
                String str = scanner.nextLine();
                try {
                    invokeLine(str);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } catch (WrongNumberOfArgumentsException e) {
                    System.err.println(e.getMessage());
                } catch (LoadOrSaveException e) {
                    System.err.println(e.getMessage());
                }
                System.out.print("$ ");
            }
        }
    }

    private void invokeLine(String line) throws IOException, WrongNumberOfArgumentsException, LoadOrSaveException {

        String[] commandsWithArgs = line.trim().split(";");
        for (String command : commandsWithArgs) {
            String[] tokens = command.trim().split("\\s+");
            String commandName = tokens[0];
            Command cmd = commands.get(commandName);
            if (cmd == null) {
                throw new IOException(commandName + ": no such command");
            }
            String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
            cmd.invoke(dbConnector, args);
        }

    }
}