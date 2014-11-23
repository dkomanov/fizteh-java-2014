package ru.fizteh.fivt.students.vadim_mazaev.Interpreter;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.Callable;

public final class Interpreter {
    public static final String PROMPT = "$ ";
    public static final String NO_SUCH_COMMAND_MSG = "No such command declared: ";
    private static final String IGNORE_IN_ROUND_BRACKETS_REGEX = "(?![^\\(]*\\))";
    private static final String IGNORE_IN_SQUARE_BRACKETS_REGEX = "(?![^\\[]*\\])";
    private static final String IGNORE_IN_DOUBLE_QUOTES_REGEX = "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String IGNORE_SYMBOL_IN_TOKENS_REGEX = IGNORE_IN_ROUND_BRACKETS_REGEX
            + IGNORE_IN_SQUARE_BRACKETS_REGEX + IGNORE_IN_DOUBLE_QUOTES_REGEX;
    public static final String COMMAND_SEPARATOR = ";" + IGNORE_IN_DOUBLE_QUOTES_REGEX;
    private InputStream in;
    private PrintStream out;
    private Object connector;
    private Map<String, Command> commands;
    private Callable<Boolean> exitHandler;
    
    public Interpreter(Object connector, Command[] commands, InputStream in, PrintStream out) {
        if (in == null || out == null) {
            throw new IllegalArgumentException("Input or Output stream is null");
        }
        this.commands = new HashMap<>();
        this.in = in;
        this.out = out;
        this.connector = connector;
        for (Command cmd : commands) {
            this.commands.put(cmd.getName(), cmd);
        }
    }
    
    public Interpreter(Object connector, Command[] commands) {
        this(connector, commands, System.in, System.out);
    }
    
    public int run(String[] args) throws Exception {
        int exitStatus = 0;
        try {
            if (args.length == 0) {
                exitStatus = interactiveMode();
            } else {
                exitStatus = batchMode(args);
            }
        } catch (StopInterpreterException e) {
            exitStatus = e.getExitStatus();
        }
        return exitStatus;
    }
    
    public void setExitHandler(Callable<Boolean> callable) {
        exitHandler = callable;
    }
    
    private int batchMode(String[] args) throws Exception {
        int exitStatus = executeLine(String.join(" ", args));
        if (exitHandler != null) {
            exitHandler.call();
        }
        return exitStatus;
    }

    private int interactiveMode() throws Exception {
        int exitStatus = 0;
        try (Scanner in = new Scanner(this.in)) {
            while (true) {
                out.print(PROMPT);
                try {
                    exitStatus = executeLine(in.nextLine().trim());
                } catch (NoSuchElementException e) {
                    break;
                } catch (StopInterpreterException e) {
                    if (e.getExitStatus() == 0) {
                        break;
                    }
                }
            }
        }
        return exitStatus;
    }
    
    private int executeLine(String line) throws Exception {
        String[] cmds = line.split(COMMAND_SEPARATOR);
        try {
            for (String current : cmds) {
                    parse(current.trim().split("\\s+" + IGNORE_SYMBOL_IN_TOKENS_REGEX));
            }
            return 0;
        } catch (StopLineInterpretationException e) {
            out.println(e.getMessage());
            return 1;
        }
    }
    
    private void parse(String[] cmdWithArgs) throws Exception {
        if (cmdWithArgs.length > 0 && !cmdWithArgs[0].isEmpty()) {
            String commandName = cmdWithArgs[0];
            if (commandName.equals("exit")) {
                if (exitHandler == null) {
                    throw new StopInterpreterException(0);
                }
                if (exitHandler.call()) {
                    throw new StopInterpreterException(0);
                } else {
                    throw new StopInterpreterException(1);
                }
            }
            Command command = commands.get(commandName);
            if (command == null) {
                throw new StopLineInterpretationException(NO_SUCH_COMMAND_MSG + commandName);
            } else {
                String[] args = new String[cmdWithArgs.length - 1];
                //Exclude quotes along the edges of the string, if they presents.
                for (int i = 1; i < cmdWithArgs.length; i++) {
                    if (cmdWithArgs[i].charAt(0) == '"'
                            && cmdWithArgs[i].charAt(cmdWithArgs[i].length() - 1) == '"') {
                        args[i - 1] = cmdWithArgs[i].substring(1, cmdWithArgs[i].length() - 1);
                    } else {
                        args[i - 1] = cmdWithArgs[i];
                    }
                }
                try {
                    command.execute(connector, args);
                } catch (RuntimeException e) {
                    throw new StopLineInterpretationException(e.getMessage());
                }
            }
        }
    }
}
