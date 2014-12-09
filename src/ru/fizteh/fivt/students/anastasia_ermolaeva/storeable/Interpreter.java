package ru.fizteh.fivt.students.anastasia_ermolaeva.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Command;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.ExitException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.IllegalCommandException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.NoActiveTableException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Interpreter {
    public static final String PROMPT = "$ ";
    public static final String STATEMENT_DELIMITER = ";";
    public static final String PARAM_DELIMITER = "\\s+";
    public static final String COMMAND_NOT_FOUNG_MSG = "Command not found: ";
    public static final String EXIT_COMMAND = "exit";

    private final Map<String, Command> commands;
    private final Object tableState;
    private InputStream in;
    private PrintStream out;

    public Interpreter(final Object tableState,
                       final Command[] commands,
                       final InputStream inStream,
                       final PrintStream outStream) {
        if (inStream == null || outStream == null) {
            throw new
                    IllegalArgumentException("Given streams are not valid");
        }
        this.tableState = tableState;
        in = inStream;
        out = outStream;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public Interpreter(final Object tableState, final Command[] commands) {
        this.in = System.in;
        this.out = System.out;
        this.tableState = tableState;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public final void run(final String[] arguments) throws ExitException {
        try {
            if (arguments.length == 0) {
                userMode();
            } else {
                batchMode(arguments);
            }
        } catch (ExitException e) {
            if (!e.getMessage().equals("")) {
                out.println(e.getMessage());
            }
            throw e;
        }
    }

    private void batchMode(final String[] args) throws ExitException {
        StringBuilder cmd = new StringBuilder();
        for (String arg : args) {
            cmd.append(arg);
            cmd.append(' ');
        }
        String[] commands = cmd.toString().trim().split(STATEMENT_DELIMITER);
        for (String command : commands) {
            commandHandler(command, false);
        }
        commandHandler(EXIT_COMMAND, false);
        throw new ExitException(0);
    }

    private void userMode() throws ExitException {
        try (Scanner scan = new Scanner(in)) {
            while (true) {
                out.print(PROMPT);
                String line = "";
                try {
                    line = scan.nextLine();
                } catch (NoSuchElementException e) {
                    throw new ExitException(0);
                }
                String[] commands = line.trim().split(STATEMENT_DELIMITER);
                try {
                    for (String command : commands) {
                        commandHandler(command, true);
                    }
                } catch (IllegalCommandException | NoActiveTableException e) {
                    out.println(e.getMessage());
                    continue;
                } catch (IllegalStateException s) {
                    continue;
                }
            }
        } catch (NoSuchElementException e) {
            throw new ExitException(e.getMessage(), 1);
        }
    }

    private void handleCommand(String commandName, boolean userMode, String[] arguments) throws ExitException {
        Command command = commands.get(commandName);
        if (command == null) {
            if (userMode) {
                throw new IllegalCommandException(COMMAND_NOT_FOUNG_MSG + commandName);
            } else {
                throw new ExitException(COMMAND_NOT_FOUNG_MSG + commandName, 1);
            }
        } else {
            try {
                command.execute(tableState, arguments);
            } catch (IllegalCommandException | NoActiveTableException |
                    IndexOutOfBoundsException | ColumnFormatException e) {
                if (userMode) {
                    if (e.getClass().equals(IllegalCommandException.class)
                            || e.getClass().equals(NoActiveTableException.class)) {
                        throw e;
                    } else {
                        throw new IllegalCommandException(e.getMessage());
                    }
                } else {
                    throw new ExitException(e.getMessage(), 1);
                }
            } catch (IllegalArgumentException a) {
                if (userMode) {
                    out.println(a.getMessage());
                } else {
                    throw new ExitException(a.getMessage(), 1);
                }
            } catch (IllegalStateException s) {
                if (userMode) {
                    throw s;
                } else {
                    throw new ExitException(1);
                }
            }
        }
    }

    private void commandHandler(String cmd,
                                boolean userMode) throws ExitException {
        String[] arguments = cmd.trim().split(PARAM_DELIMITER);
        if ((arguments.length > 0) && !arguments[0].isEmpty()) {
            String commandName = arguments[0];
            handleCommand(commandName, userMode, arguments);
        }
    }
}
