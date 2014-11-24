package ru.fizteh.fivt.students.PotapovaSofia.JUnit.Interpreter;

import ru.fizteh.fivt.students.PotapovaSofia.JUnit.StopInterpretationException;
import ru.fizteh.fivt.students.PotapovaSofia.JUnit.TableState;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;


public class Interpreter {
    public static final String PROMPT = "$ ";
    public static final String STATEMENT_DELIMITER = ";";
    public static final String PARAM_REGEXP = "\\S+";
    public static  final String COMMAND_NOT_FOUND_MSG = "Command not found: ";

    private InputStream in;
    private PrintStream out;

    private final Map<String, Command> commands;
    private final TableState state;

    public Interpreter(TableState state, Command[] commands, InputStream in, PrintStream out) {
        if (in == null || out == null) {
            throw new IllegalArgumentException("Input or Output stream is null");
        }
        this.in = in;
        this.out = out;
        this.state = state;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public Interpreter(TableState state, Command[] commands) {
        this.state = state;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public void run(String[] args) {
        try {
            if (args.length == 0) {
                runInteractiveMode();
            } else {
                runBatchMode(args);
            }
        } catch (StopInterpretationException e) {
            // Just stop the interpretation.
        }
    }

    private void runBatchMode(String[] args) throws StopInterpretationException {
        executeLine(String.join(" ", args));
    }

    private void runInteractiveMode() throws StopInterpretationException {
        Scanner in = new Scanner(this.in);
        while (true) {
            out.print(PROMPT);
            try {
                String line = in.nextLine();
                executeLine(line);
            } catch (NoSuchElementException e) {
                break;
            }
        }
    }

    private void executeLine(String line) throws StopInterpretationException {
        String[] statements = line.split(STATEMENT_DELIMITER);
        for (String statement : statements) {
            String[] chunks = Utils.findAll(PARAM_REGEXP, statement);
            if (chunks.length != 0) {
                String commandName = chunks[0];
                String[] params = Arrays.copyOfRange(chunks, 1, chunks.length);
                Command command = commands.get(commandName);
                if (commandName.equals("exit")) {
                    throw new StopInterpretationException();
                }
                if (command == null) {
                    Utils.interpreterError(COMMAND_NOT_FOUND_MSG + commandName);
                } else {
                    command.execute(this.state, params);
                }
            }
        }
    }
}
