package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.io.IOException;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.*;

public class Shell<State> {
    private HashMap<String, Command> commands = new HashMap<String, Command>();
    private String[] args;
    private String prompt = "$ ";
    private State shellState;

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void start() {
        if (args == null || args.length == 0) {
            startInteractive();
        } else {
            packageMode();
        }
    }

    public void setShellState(State shellState) {
        this.shellState = shellState;
    }

    public void setCommands(List<Command<?>> commands) {
        for (final Command<?> command : commands) {
            this.commands.put(command.getCommandName(), command);
        }
    }

    private void startInteractive() {
        Scanner scanner = new Scanner(System.in);

        printPrompt();

        while (scanner.hasNext()) {
            String command = scanner.nextLine();
            String[] commands = CommandParser.parseCommands(command);

            if (commands.length == 0) {
                System.out.println("no commands");
            }

            for (final String com : commands) {
                if (!processCommand(com)) {
                    break;
                }
            }

            printPrompt();
        }
    }

    private void packageMode() {
        StringBuilder sb = new StringBuilder();

        for (final String st : args) {
            sb.append(st + " ");
        }

        String[] commands = CommandParser.parseCommands(sb.toString());

        for (final String command : commands) {
            boolean status = processCommand(command);

            if (!status) {
                System.exit(-1);
            }
        }
    }

    private void printPrompt() {
        System.out.print(prompt);
    }

    private boolean processCommand(String command) {
        String commandName = CommandParser.getCommandName(command);
        String params = CommandParser.getParameters(command);

        if (commandName.equals("")) {
            System.err.println("empty command!");
            return true;
        }

        if (!commands.containsKey(commandName)) {
            System.err.println(String.format("%s: command not found. Type help to get help", commandName));
            return false;
        }

        boolean status = true;

        try {
            commands.get(commandName).executeCommand(params, shellState);
        } catch (IllegalArgumentException e) {
            System.err.println(commandName + ": " + e.getMessage());
            status = false;
        } catch (IOException e) {
            System.err.println(commandName + ": " + e.getMessage());
            status = false;
        }

        return status;
    }
}
