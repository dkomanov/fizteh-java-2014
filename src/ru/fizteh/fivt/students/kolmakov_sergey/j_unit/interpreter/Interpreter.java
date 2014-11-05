package ru.fizteh.fivt.students.kolmakov_sergey.j_unit.interpreter;

import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_exceptions.WrongNumberOfArgumentsException;
import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_structure.DataBaseState;
import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_exceptions.StopInterpreterException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

public final class Interpreter {
    public static final String PROMPT = "$ ";
    public final static String BAD_COMMAND = "Command not found: ";
    public static final String STATEMENT_DELIMITER = ";";
    public static final String EXIT_WITHOUT_HANDLER_MSG = "You must set exitHandler before using exit command";
    private final String PARAM_REGEXP = "\\s+";
    private InputStream in;
    private PrintStream out;
    private DataBaseState dbState;
    private Map<String, Command> commands;
    private Callable<Boolean> exitHandler;

    public Interpreter(DataBaseState dbState, Command[] commands, InputStream in, PrintStream out) {
        if (in == null || out == null) {
            throw new IllegalArgumentException("Input or Output stream is null");
        }
        this.commands = new HashMap<>();
        this.in = in;
        this.out = out;
        this.dbState = dbState;
        for (Command currentCommand : commands) {
            this.commands.put(currentCommand.getName(), currentCommand);
        }
    }

    public Interpreter(DataBaseState connector, Command[] commands) {
        this(connector, commands, System.in, System.out);
    }

    public void setExitHandler(Callable<Boolean> callable) {
        exitHandler = callable;
    }

    public int run(String[] args) throws Exception {
        try {
            if (args.length == 0) {
                interactiveMode();
            } else {
                batchMode(args);
            }
        } catch (StopInterpreterException e) {
          return e.exitCode; //  Just stop Interpreter.
        }
        return 0;
    }

    private void batchMode(String[] args) throws Exception {
        StringBuilder builder = new StringBuilder();
        for (String current : args) {
            builder.append(current);
            builder.append(" ");
        }
        executeLine(builder.toString(), true);
    }

    private void interactiveMode() throws Exception {
        try (Scanner in = new Scanner(this.in)) {
            while (true) {
                out.print(PROMPT);
                executeLine(in.nextLine().trim(), false);
            }
        }
    }

    private void executeLine(String line, boolean batchModeOn) throws Exception {
        String[] commands = line.split(STATEMENT_DELIMITER);
        try {
            for (String current : commands) {
                parse(current.trim().split(PARAM_REGEXP), batchModeOn);
            }
            if (batchModeOn){
                parse(new String[]{"exit"}, batchModeOn);
            }
        } catch (WrongNumberOfArgumentsException e){
            out.println(e.getMessage());
            if (batchModeOn) {
                throw new StopInterpreterException(-1);
            }
        }
    }

    private void parse(String[] cmdWithArgs, boolean batchModeOn) throws Exception {
        if (cmdWithArgs.length > 0 && !cmdWithArgs[0].isEmpty()) {
            String commandName = cmdWithArgs[0];
            if (commandName.equals("exit")) {
                if (exitHandler == null) {
                    out.println(EXIT_WITHOUT_HANDLER_MSG);
                    throw new StopInterpreterException(-1);
                }
                if (exitHandler.call()) {
                    throw new StopInterpreterException(0);
                } else if (batchModeOn){
                    throw  new StopInterpreterException(-1);
                }
                return;
            }
            Command command = commands.get(commandName);
            if (command == null) {
                out.println(BAD_COMMAND + commandName);
                if (batchModeOn){
                    throw new StopInterpreterException(-1);
                }
            } else {
                String[] cuttedArgs = Arrays.copyOfRange(cmdWithArgs, 1, cmdWithArgs.length);
                try {
                    command.execute(dbState, cuttedArgs);
                } catch (RuntimeException e){
                    out.println(e.getMessage());
                    if (batchModeOn){
                        throw new StopInterpreterException(-1);
                    }
                }
            }
        }
    }
}