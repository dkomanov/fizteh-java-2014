package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter;

import ru.fizteh.fivt.students.pavel_voropaev.project.Utils;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.StopInterpretationException;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class Interpreter {
    private static final String PROMPT = "$ ";
    private static final String STATEMENT_DELIMITER = ";";

    private InterpreterState state;
    private final Map<String, Command> commands;

    // Provides you the ability to stop interpretation even if you forgot to include custom exit.
    public class DefaultExit extends AbstractCommand {
        public DefaultExit(InterpreterState state) {
            super("exit", 0, state);
        }

        @Override
        public void exec(String[] param) {
        }
    }

    public Interpreter(Command[] commands, InterpreterState state) {
        if (state.getInputStream() == null || state.getOutputStream() == null || state.getErrorStream() == null) {
            throw new IllegalArgumentException("One of the iostreams is not initialized");
        }

        this.state = state;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
        if (!this.commands.containsKey("exit")) {
            this.commands.put("exit", new DefaultExit(state));
        }
    }

    public void run(String[] args) throws IOException {
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

    private void runBatchMode(String[] args) throws IOException {
        try {
            executeLine(String.join(" ", args));
            if (!state.isExitSafe()) {
                state.getErrorStream().println("Exit without saving!");
            }
        } catch (StopInterpretationException e) {
            // Exit with saving.
        }
    }

    private void runInteractiveMode() throws StopInterpretationException {
        Scanner scan = new Scanner(state.getInputStream());
        while (true) {
            state.getOutputStream().print(PROMPT);
            try {
                String line = scan.nextLine();
                executeLine(line);
            } catch (NoSuchElementException e) {
                if (!state.isExitSafe()) {
                    state.getErrorStream().println("Exit without saving!");
                }
                break;
            } catch (IOException | InputMistakeException e) {
                state.getErrorStream().println(e.getMessage());
            }
        }
        scan.close();
    }

    private void executeLine(String line) throws StopInterpretationException, IOException {
        String[] statements = line.split(STATEMENT_DELIMITER);
        String[] chunks;
        for (String statement : statements) {
            try {
                chunks = Utils.splitArguments(statement);
            } catch (ParseException e) {
                throw new InputMistakeException("Wrong command: " + e.getMessage());
            }

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

                command.exec(params);

                if (commandName.equals("exit")) {
                    throw new StopInterpretationException();
                }
            }
        }
    }
}
