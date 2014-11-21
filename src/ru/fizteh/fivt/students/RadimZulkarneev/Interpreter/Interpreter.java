package ru.fizteh.fivt.students.RadimZulkarneev.Interpreter;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Interpreter {
    private final String PROMPT = "$ ";
    private final String STATEMENT_DELIMITER = ";";
    private final String PARAM_REGEXP = "\\S+";
    private InputStream inputStream;
    private PrintStream printStream;
    private InterpreterState interpreterState;
    private final Map<String, Command> commands;

    public Interpreter (InterpreterState dbState, Command[] commands, InputStream in, PrintStream out) {
        this.commands = new HashMap<>();
        if (commands == null || in == null || out == null) {
            throw new IllegalArgumentException("argument is null");
        }
        this.inputStream = in;
        this.printStream = out;
        this.interpreterState = dbState;
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public void run(String [] arg) {
        try {
            if (arg.length == 0) {
                runInteractiveMode();
            } else {
            runBatchMode(arg);
            }
        } catch (InterruptedException e) {
            // To stop interpreter.
        }
    }

    private void runBatchMode(String[] args) throws InterruptedException {
        executeLine(String.join(" ", args), true);
    }

    public void runInteractiveMode() throws InterruptedException {
        try (Scanner in = new Scanner(this.inputStream)) {
            while (true) {
                printStream.print(PROMPT);
                try {
                    executeLine(in.nextLine(), false);
                } catch (NoSuchElementException e) {
                    break;
                }
            }
        }
    }

    private void executeLine(String line, boolean stopOnError) throws InterruptedException {
        String[] statements = line.split(STATEMENT_DELIMITER);
        for (String statement : statements) {
            String[] chunks = Utility.findAll(PARAM_REGEXP, statement);

            String commandName = chunks[0];
            String[] params = Arrays.copyOfRange(chunks, 1, chunks.length);
            Command command = commands.get(commandName);
            if (commandName.equals("exit")) {
                throw new InterruptedException();
            }
            if (command == null) {
                System.out.println("Command not found: " + commandName);
            } else {
                try {
                    command.execute(this.interpreterState, params);
                } catch (IllegalArgumentException e) {
                    printStream.println(e.getMessage());
                    if (stopOnError) {
                        throw new InterruptedException();
                    }
                }
            }
        }
    }
}
