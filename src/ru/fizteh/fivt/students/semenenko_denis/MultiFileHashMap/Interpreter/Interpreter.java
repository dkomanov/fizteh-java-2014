package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap.Interpreter;

import java.util.*;

public class Interpreter {
    private static final String PROMPT = "$ ";
    private static final String STATEMENT_DELIMITER = ";";
    private static final String PARAM_REGEXP = "\\S+";

    private final Map<String, Command> commands;
    private final InterpreterState interpreterState;

    public Interpreter(InterpreterState interpreterState, Command[] commands) {
        this.interpreterState = interpreterState;
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
        executeLine("exit");
    }

    private void runInteractiveMode() throws StopInterpretationException {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print(PROMPT);
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

            String commandName = chunks[0];
            String[] params = Arrays.copyOfRange(chunks, 1, chunks.length);
            Command command = commands.get(commandName);
           /* if (commandName.equals("exit")) {
                throw new StopInterpretationException();
            }*/
            if (command == null) {
                Utils.interpreterError("Command not found: " + commandName);
            } else {
                command.execute(this.interpreterState, params);
            }
            if (interpreterState.exited()) {
                throw new StopInterpretationException();
            }
        }
    }
}
