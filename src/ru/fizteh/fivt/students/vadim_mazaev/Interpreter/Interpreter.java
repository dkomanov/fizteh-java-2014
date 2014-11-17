package ru.fizteh.fivt.students.vadim_mazaev.Interpreter;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.Callable;

public final class Interpreter {
    public static final String PROMPT = "$ ";
    public static final String COMMAND_SEPARATOR = ";";
    public static final String NO_SUCH_COMMAND_MSG = "No such command declared: ";
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
        StringBuilder builder = new StringBuilder();
        for (String current : args) {
            builder.append(current);
            builder.append(" ");
        }
        int exitStatus = executeLine(builder.toString());
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
                    parse(current.trim().split("\\s+"));
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
                String[] args = Arrays.copyOfRange(cmdWithArgs, 1, cmdWithArgs.length);
                try {
                    command.execute(connector, args);
                } catch (RuntimeException e) {
                    throw new StopLineInterpretationException(e.getMessage());
                }
            }
        }
    }
}
