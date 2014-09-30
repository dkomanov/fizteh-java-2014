package ru.fizteh.fivt.students.sautin1.shell;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static ru.fizteh.fivt.students.sautin1.shell.CommandParser.splitCommandIntoParams;
import static ru.fizteh.fivt.students.sautin1.shell.CommandParser.splitStringIntoCommands;

/**
 * Created by sautin1 on 9/30/14.
 */
public class Shell {
    private final Map<String, Command> commandsMap = new HashMap<String, Command>();

    {
        Command command;
        command = new CommandCat();
        commandsMap.put(command.toString(), command);
        command = new CommandCd();
        commandsMap.put(command.toString(), command);
        command = new CommandCp();
        commandsMap.put(command.toString(), command);
        command = new CommandLs();
        commandsMap.put(command.toString(), command);
        command = new CommandMkDir();
        commandsMap.put(command.toString(), command);
        command = new CommandMv();
        commandsMap.put(command.toString(), command);
        command = new CommandPwd();
        commandsMap.put(command.toString(), command);
        command = new CommandRm();
        commandsMap.put(command.toString(), command);
        command = new CommandExit();
        commandsMap.put(command.toString(), command);
    }

    /**
     * Tries executing command with parameters commandWithParams.
     * @param commandWithParams - Array of command parameters. Zero-element is a command.
     * @return true, if the command is "exit"; false, otherwise.
     */
    public boolean executeCommand(String... commandWithParams) throws IllegalArgumentException {
        System.out.println("Trying to exec \'" + commandWithParams[0] + "\'");
        Command command = commandsMap.get(commandWithParams[0]);
        if (command == null) {
            throw new IllegalArgumentException(commandWithParams[0] + ": command not found");
        }
        if (command instanceof CommandExit) {
            return true;
        }
        try {
            command.execute(commandWithParams);
        } catch (IllegalArgumentException e) {

        }
        return false;
    }

    /**
     * Parses string newCommand into commands and tries executing them.
     * @param newCommand - String consisting of one or a few commands (divided by ;)
     * @return true, if there was command "exit" in newCommand; false, otherwise.
     */
    public boolean callCommands(String newCommand) {
        String[] commandArray = splitStringIntoCommands(newCommand);
        for (String command : commandArray) {
            String[] params = splitCommandIntoParams(command);
            boolean wantsExit = false;
            try {
                wantsExit = executeCommand(params);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                System.out.println(e.getMessage());
                return false;
            } finally {
                if (wantsExit) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Asks user to enter commands and executes them.
     */
    public void interactWithUser() {
        Scanner scanner = new Scanner(System.in);
        boolean wantExit = false;
        while (true) {
            System.out.print("$ ");
            String newCommand = scanner.nextLine();
            if (newCommand.isEmpty()) {
                continue;
            }
            wantExit = callCommands(newCommand);
            if (wantExit) {
                break;
            }
        }
    }
}
