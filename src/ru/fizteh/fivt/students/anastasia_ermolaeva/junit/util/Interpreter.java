package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Interpreter {
    public static final String PROMPT = "$ ";
    public static final String STATEMENT_DELIMITER = ";";
    public static final String PARAM_REGEXP = "\\s+";
    public static final String ERR_MSG = "Command not found: ";
    private final Map<String, Command> commands;
    private final TableState tableState;
    private InputStream in;
    private PrintStream out;
    private PrintStream err;

    public Interpreter(final TableState tableState,
                       final Command[] commands,
                       final InputStream inStream,
                       final PrintStream outStream,
                       final PrintStream errStream) {
        if (inStream == null || outStream == null || errStream == null) {
            throw new
                    IllegalArgumentException("Given streams are not valid");
        }
        this.tableState = tableState;
        in = inStream;
        out = outStream;
        err = errStream;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public Interpreter(final TableState tableState, final Command[] commands) {
        this.in = System.in;
        this.out = System.out;
        this.err = System.err;
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
        String currentTableName = tableState.getCurrentTableName();
        tableState.getTableHolder().getTable(currentTableName).commit();
        //handleCommand("commit", true, new String[]{"commit"});
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
                throw new IllegalCommandException(ERR_MSG + commandName);
            } else {
                throw new ExitException(ERR_MSG + commandName, 1);
            }
        } else {
            try {
                command.execute(tableState, arguments);
            } catch (IllegalCommandException |NoActiveTableException e) {
                if (userMode) {
                    throw e;
                } else {
                    throw new ExitException(e.getMessage(), 1);
                }
            }
        }
    }

    private void commandHandler(String cmd,
                                boolean userMode) throws ExitException {
        String[] arguments = cmd.trim().split(PARAM_REGEXP);
        if (arguments[0].equals("show")) {
            String cmdName = "show";
            if (arguments.length > 1) {
                cmdName = "show " + arguments[1];
                String[] newArguments = new String[arguments.length - 1];
                newArguments[0] = cmdName;
                for (int i = 1; i < arguments.length - 1; i++) {
                    newArguments[i] = arguments[i + 1];
                }
                handleCommand(cmdName, userMode, newArguments);
            } else {
                handleCommand(cmdName, userMode, arguments);
            }
        } else {
            if ((arguments.length > 0) && !arguments[0].isEmpty()) {
                String commandName = arguments[0];
                handleCommand(commandName, userMode, arguments);
            }
        }
    }
}
