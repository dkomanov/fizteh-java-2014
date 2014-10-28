package ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap.util;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Interpreter {
    private final Map<String, Command> commands;
    private final TableState tableState;

    public Interpreter(final TableState tableState, final Command[] commands) {
        this.tableState = tableState;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }
    public final void run(final String[] args) {
        try {
            if (args.length == 0) {
                userMode();
            } else {
                batchMode(args);
            }
        } catch (ExitException e) {
            System.exit(e.getStatus());
        }
    }

    private final void batchMode(final String[] args) throws ExitException {
        StringBuilder cmd = new StringBuilder();
        for (String arg: args) {
            cmd.append(arg);
            cmd.append(' ');
        }
        cmd.append("exit");
        String[] commands = cmd.toString().trim().split(";");
        for (String command:commands) {
            commandHandler(command, false);
        }
    }

    private void userMode() throws ExitException {
        try (Scanner scan = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                String line = "";
                try {
                    line = scan.nextLine();
                } catch (NoSuchElementException e) {
                    System.exit(0);
                }
                String[] commands = line.trim().split(";");
                for (String command:commands) {
                    commandHandler(command, true);
                }
            }
        } catch (NoSuchElementException e) {
            throw new ExitException(1);
        }
    }

    private void commandHandler(String cmd,
                                  boolean userMode) throws ExitException {
        String[] arguments = cmd.trim().split("\\s+");
        if (arguments[0].equals("show")) {
                String cmdName = "show";
                if (arguments.length > 1) {
                    cmdName = "show " + arguments[1];
                    String[] newArguments = new String[arguments.length - 1];
                    newArguments[0] = cmdName;
                    for (int i = 1; i < arguments.length - 1; i++) {
                        newArguments[i] = arguments[i + 1];
                    }
                    Command command = commands.get(cmdName);
                    if (command == null) {
                        System.out.println("Command not found: " + cmdName);
                        if (!userMode) {
                            throw new ExitException(1);
                        }
                    } else {
                        try {
                            command.execute(tableState, newArguments);
                        } catch (IllegalNumberOfArgumentsException e) {
                            if (!userMode) {
                                throw new ExitException(1);
                            }
                        }
                    }
                } else {
                    Command command = commands.get(cmdName);
                    if (command == null) {
                        System.out.println("Command not found: " + cmdName);
                        if (!userMode) {
                            throw new ExitException(1);
                        }
                    } else {
                        try {
                            command.execute(tableState, arguments);
                        } catch (IllegalNumberOfArgumentsException e) {
                            if (!userMode) {
                                throw new ExitException(1);
                            }
                        }
                    }
                }
        } else {
            try {
                if ((arguments.length > 0) && !arguments[0].isEmpty()) {
                    String commandName = arguments[0];
                    Command command = commands.get(commandName);
                    if (command == null) {
                        System.out.println("Command not found: " + commandName);
                        if (!userMode)
                            throw new ExitException(1);
                    } else {
                        try {
                            command.execute(tableState, arguments);
                        } catch (IllegalNumberOfArgumentsException e) {
                            if (!userMode) {
                                throw new ExitException(1);
                            }
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                if (!userMode)
                    throw new ExitException(1);
            }
        }
    }
}
