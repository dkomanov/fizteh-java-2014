package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter;

import java.io.PrintStream;
import java.util.*;
import java.io.InputStream;


import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.*;
import ru.fizteh.fivt.students.pavel_voropaev.project.Utils;


public class Interpreter {
    private static final String PROMPT = "$ ";
    private static final String STATEMENT_DELIMITER = ";";
    private static final String PARAM_REGEXP = "\\S+";

    private InputStream in;
    private PrintStream out;
    private PrintStream err;

    private final Map<String, Command> commands;

    public Interpreter(Command[] commands, InputStream in, PrintStream out, PrintStream err) {
        if (in == null || out == null || err == null) {
            throw new IllegalArgumentException("One of the iostreams is not initialized.");
        }

        this.in = in;
        this.out = out;
        this.err = err;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public Interpreter(Command[] commands) {
        this(commands, System.in, System.out, System.err);
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

    private void runBatchMode(String[] args) {
        try {
            executeLine(String.join(" ", args));
            err.println("Exit without saving!");
        } catch (InputMistakeException e) {
            out.println(e.getMessage());
            System.exit(1);
        } catch (StopInterpretationException e) {
            // Exit with saving.
        }

    }

    private void runInteractiveMode() throws StopInterpretationException {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print(PROMPT);
            try {
                String line = in.nextLine();
                executeLine(line);
            } catch (NoSuchElementException e) {
                err.println("Exit without saving!");
                break;
            } catch (InputMistakeException e) {
                err.println(e.getMessage());
            }
        }
        in.close();
    }

    private void executeLine(String line) throws StopInterpretationException, InputMistakeException {
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
                    throw new InputMistakeException(commandName + ": too many arguments.");
                }
                if (command.getArgsNum() > params.length) {
                    throw new InputMistakeException(commandName + ": not enough arguments.");
                }

                command.exec(params, out);

                if (commandName.equals("exit")) {
                    throw new StopInterpretationException();
                }
            }
        }
    }
}
