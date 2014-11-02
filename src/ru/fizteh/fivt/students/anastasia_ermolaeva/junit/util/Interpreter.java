package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.Command;
import ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap.util.ExitException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap.util.IllegalNumberOfArgumentsException;

public class Interpreter {
    public static final String PROMPT = "$ ";
    public static final String STATEMENT_DELIMITER = ";";
    public static final String PARAM_REGEXP = "\\s+";
    public static final String ERR_MSG = "Command not found: ";
    private final Map<String, Command> commands;
    private final TableState tableState;
    private InputStream in;
    private PrintStream out;

    public Interpreter(final TableState tableState,
                       final Command[] commands,
                       final InputStream inStream,
                       final PrintStream outStream) {
        if (inStream == null || outStream == null) {
            throw new
                    IllegalArgumentException("Input or "
                    + "output stream is null");
        }
        this.tableState = tableState;
        in = inStream;
        out = outStream;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }
    public Interpreter(final TableState tableState, final Command[] commands) {
        this.in = System.in;
        this.out = System.out;
        this.tableState = tableState;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }
    public final void run(final String[] arguments) throws ExitException {
        if (arguments.length == 0) {
                userMode();
        } else {
            batchMode(arguments);
        }
    }

    private final void batchMode(final String[] args) throws ExitException {
        StringBuilder cmd = new StringBuilder();
        for (String arg: args) {
            cmd.append(arg);
            cmd.append(' ');
        }
        String[] commands = cmd.toString().trim().split(STATEMENT_DELIMITER);
        for (String command:commands) {
            commandHandler(command, false);
        }
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
                for (String command:commands) {
                    commandHandler(command, true);
                }
            }
        } catch (NoSuchElementException e) {
            throw new ExitException(1);
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
                Command command = commands.get(cmdName);
                if (command == null) {
                    out.println(ERR_MSG + cmdName);
                    if (!userMode) {
                        throw new ExitException(1);
                    }
                } else {
                    try {
                        command.execute(tableState, newArguments);
                    } catch (IllegalNumberOfArgumentsException e) {
                        if (!userMode) {
                            throw new ExitException(1);
                        }
                    }
                }
            } else {
                Command command = commands.get(cmdName);
                if (command == null) {
                    out.println(ERR_MSG + cmdName);
                    if (!userMode) {
                        throw new ExitException(1);
                    }
                } else {
                    try {
                        command.execute(tableState, arguments);
                    } catch (IllegalNumberOfArgumentsException e) {
                        if (!userMode) {
                            throw new ExitException(1);
                        }
                    }
                }
            }
        } else {
            try {
                if ((arguments.length > 0) && !arguments[0].isEmpty()) {
                    String commandName = arguments[0];
                    Command command = commands.get(commandName);
                    if (command == null) {
                        out.println(ERR_MSG + commandName);
                        if (!userMode)
                            throw new ExitException(1);
                    } else {
                        try {
                            command.execute(tableState, arguments);
                        } catch (IllegalNumberOfArgumentsException e) {
                            if (!userMode) {
                                throw new ExitException(1);
                            }
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                if (!userMode)
                    throw new ExitException(1);
            }
        }
    }
}
