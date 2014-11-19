package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter;

import ru.fizteh.fivt.students.pavel_voropaev.project.Utils;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.StopInterpretationException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class Interpreter {
    private static final String PROMPT = "$ ";
    private static final String STATEMENT_DELIMITER = ";";
    private static final String PARAM_REGEXP = "\\S+";

    private InputStream in;
    private PrintStream out;
    private PrintStream err;

    private final Map<String, Command> commands;

    // Provides you the ability to stop interpretation even if you forgot to include custom exit.
    public class DefaultExit extends AbstractCommand<Object> {
        public DefaultExit() {
            super("exit", 0, new Object());
        }
        @Override
        public void exec(String[] param, PrintStream out) {
        }
    }

    public Interpreter(Command[] commands, InputStream in, PrintStream out, PrintStream err) {
        if (in == null || out == null || err == null) {
            throw new IllegalArgumentException("One of the iostreams is not initialized");
        }

        this.in = in;
        this.out = out;
        this.err = err;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
        if (!this.commands.containsKey("exit")) {
            this.commands.put("exit", new DefaultExit());
        }
    }

    public Interpreter(Command[] commands) {
        this(commands, System.in, System.out, System.err);
    }

    public void run(String[] args) {
        if (args.length == 0) {
            try {
                runInteractiveMode();
            } catch (StopInterpretationException e) {
                // Exit.
            }
        } else {
            runBatchMode(args);
        }
    }

    private void runBatchMode(String[] args) {
        try {
            executeLine(String.join(" ", args));
            err.println("Exit without saving!");
        } catch (StopInterpretationException e) {
            // Exit with saving.
        }
    }

    private void runInteractiveMode() throws StopInterpretationException {
        Scanner scan = new Scanner(in);
        while (true) {
            out.print(PROMPT);
            try {
                String line = scan.nextLine();
                executeLine(line);
            } catch (NoSuchElementException e) {
                err.println("Exit without saving!");
                break;
            } catch (InputMistakeException e) {
                err.println(e.getMessage());
            }
        }
        scan.close();
    }

    private void executeLine(String line) throws StopInterpretationException {
        String[] statements = line.split(STATEMENT_DELIMITER);
        for (String statement : statements) {
            String[] chunks = Utils.findAll(PARAM_REGEXP, statement);

            if (chunks.length > 0) {
                String commandName = chunks[0];
                String[] params = Arrays.copyOfRange(chunks, 1, chunks.length);
                Command command = commands.get(commandName);

                if (command == null) {
                    throw new InputMistakeException("No such command: " + commandName);
                }
                if (command.getArgsNum() < params.length) {
                    throw new InputMistakeException(commandName + ": too many arguments");
                }
                if (command.getArgsNum() > params.length) {
                    throw new InputMistakeException(commandName + ": not enough arguments");
                }

                command.exec(params, out);

                if (commandName.equals("exit")) {
                    throw new StopInterpretationException();
                }
            }
        }
    }
}
