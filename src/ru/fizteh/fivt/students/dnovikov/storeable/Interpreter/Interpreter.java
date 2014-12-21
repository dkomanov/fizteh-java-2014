package ru.fizteh.fivt.students.dnovikov.storeable.Interpreter;

import ru.fizteh.fivt.students.dnovikov.storeable.DataBaseCommand;
import ru.fizteh.fivt.students.dnovikov.storeable.DataBaseState;
import ru.fizteh.fivt.students.dnovikov.storeable.Exceptions.LoadOrSaveException;
import ru.fizteh.fivt.students.dnovikov.storeable.Exceptions.StopInterpreterException;
import ru.fizteh.fivt.students.dnovikov.storeable.Exceptions.WrongNumberOfArgumentsException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class Interpreter {

    private static final String PROMPT = "$ ";
    public static final String COMMANDS_SEPARATOR = ";";
    private final Map<String, DataBaseCommand> commands;
    private final DataBaseState state;
    private final InputStream in;
    private final PrintStream out;
    private final PrintStream err;

    public Interpreter(DataBaseState state, InputStream in, PrintStream out, PrintStream err, DataBaseCommand[] cmds) {
        if (in == null || out == null || err == null) {
            throw new IllegalArgumentException("InputStream or OutputStream is null");
        }
        this.in = in;
        this.out = out;
        this.err = err;
        this.state = state;
        this.commands = new HashMap<>();
        for (DataBaseCommand command : cmds) {
            this.commands.put(command.getName(), command);
        }
    }

    public void run(String[] args) {
        try {
            if (args.length == 0) {
                interactiveMode();
            } else {
                batchMode(args);
            }
        } catch (StopInterpreterException e) {
            // Stop the interpreter.
        }
    }

    private void batchMode(String[] args) throws StopInterpreterException {
        try {
            invokeLine(String.join(" ", args) + COMMANDS_SEPARATOR + "exit");
        } catch (IOException | LoadOrSaveException | WrongNumberOfArgumentsException e) {
            err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void invokeLine(String line) throws StopInterpreterException,
            WrongNumberOfArgumentsException, IOException {
        String[] commandsWithArgs = line.trim().split(COMMANDS_SEPARATOR);
        for (String command : commandsWithArgs) {
            String[] tokens = command.trim().split("\\s+");
            String commandName = tokens[0];
            if (commandName.equals("") || commandName.equals(COMMANDS_SEPARATOR)) {
                continue;
            }
            DataBaseCommand cmd = commands.get(commandName);
            if (cmd == null) {
                if (commandName.equals("exit")) {
                    throw new StopInterpreterException();
                } else {
                    throw new IOException(commandName + ": no such command");
                }
            }
            String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
            cmd.invoke(state, args);
        }
    }

    private void interactiveMode() throws StopInterpreterException {
        try (Scanner scanner = new Scanner(in)) {
            while (true) {
                out.print(PROMPT);
                try {
                    String str = scanner.nextLine();
                    invokeLine(str);
                } catch (IOException | WrongNumberOfArgumentsException e) {
                    err.println(e.getMessage());
                } catch (NoSuchElementException e) {
                    err.println(e.getMessage());
                    break;
                }
            }
        }
    }
}
