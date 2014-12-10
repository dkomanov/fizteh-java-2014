package ru.fizteh.fivt.students.Volodin_Denis.JUnit.interpreter;

import ru.fizteh.fivt.students.Volodin_Denis.JUnit.main.ReturnCodes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Interpreter {
    
    private final String promt = "$ ";
    private final String statementDelimiter = ";";
    private final String paramRegExp = "\\s+";
    
    private final Map<String, CommandHandler> commands;
    private final InterpreterState interpreterState;
    
    public Interpreter(InterpreterState interpreter, CommandHandler[] comms) {
        commands = new HashMap<String, CommandHandler>();
        interpreterState = interpreter;
        for (CommandHandler command : comms) {
            commands.put(command.getName(), command);
        }
    }
    
    private void executeLine(final String[] input) {
        for (int i = 0; i < input.length; ++i) {
            if (input[i].length() > 0) {
                String[] buffer = input[i].trim().split(paramRegExp);
                String commandName = buffer[0];
                if (commandName.equals("exit")) {
                    System.exit(ReturnCodes.SUCCESS);
                }
                CommandHandler command = commands.get(commandName);
                if (command == null) {
                    System.err.println("Command does not exist: [" + commandName + "]");
                    return;
                }
                try {
                    command.execute(interpreterState, Arrays.copyOfRange(buffer, 1, buffer.length));
                } catch (Exception exception) {
                    System.err.println(exception.getMessage());
                }
            }
        }
    }
    
    public void run(String[] args) {
        while (args.length == 0) {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print(promt);
                String[] input = scanner.nextLine().split(statementDelimiter);
                executeLine(input);
            } catch (Throwable exception) {
                System.err.println("Smth wrong: " + exception.getMessage());
                System.exit(ReturnCodes.ERROR);
            }
        }
        StringBuilder helpArray = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            helpArray.append(args[i]).append(' ');
        }
        String[] input = helpArray.toString().split(statementDelimiter);
        executeLine(input);
    }
}
