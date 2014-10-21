package ru.fizteh.fivt.students.SergeyAksenov.FileMap;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

//
public class Executor {
    public static boolean checkArgNumber(int from, int value, int to) {
        return from <= value && value <= to;
    }

    public static void execute(final HashMap<String, Command> commandMap,
                               String[] commands, Environment env, DataBase dataBase)
            throws FileMapExitException {
        try {
            if (commands[0].equals("")) {
                return;
            }
            Command cmd = commandMap.get(commands[0]);
            if (cmd == null) {
                ErrorHandler.unknownCommand(commands[0]);
            }
            commandMap.get(commands[0]).run(commands, dataBase, env);
        } catch (FileMapException e) {
            System.err.println(e.getMessage());
            if (env.packageMode) {
                System.exit(-1);
            }
        }
    }

    public static void execLine(String line,
                                final HashMap<String, Command> commandMap,
                                final Environment env, DataBase dataBase)
            throws FileMapExitException {
        String[] commands = line.trim().split(";");
        try {
            for (String command : commands) {
                command = command.trim();
                String[] splittedCommand = command.split("\\s+");
                execute(commandMap, splittedCommand, env, dataBase);
            }
        } catch (FileMapExitException e) {
            System.exit(0);
        }
    }

    public static void interactiveMode(
            final HashMap<String, Command> commandMap,
            final Environment env, DataBase dataBase)
            throws FileMapException, FileMapExitException {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                if (!scanner.hasNextLine()) {
                    System.exit(0);
                }
                String command = scanner.nextLine();
                execLine(command, commandMap, env, dataBase);
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void packageAppender(final String[] args,
                                       final HashMap<String, Command>
                                               commandMap,
                                       final Environment env, DataBase dataBase)
            throws FileMapExitException {
        StringBuilder commands = new StringBuilder();
        for (String arg : args) {
            commands.append(arg);
            commands.append(' ');
        }
        execLine(commands.toString(), commandMap, env, dataBase);
    }
}
