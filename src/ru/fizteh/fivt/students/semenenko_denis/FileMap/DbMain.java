package ru.fizteh.fivt.students.semenenko_denis.FileMap;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by denny_000 on 07.10.2014.
 */
public class DbMain {

    private static boolean batchMode;
    private static boolean isExit;
    private static DataBaseCache cache;

    public static void main(String[] args) {
        cache = new DataBaseCache();
        cache.init("db.file");
        if (args.length == 0) {
            batchMode = false;
            execInteractiveMode();
        } else {
            batchMode = true;
            execBatchMode(args);
        }
    }

    private static void execInteractiveMode() {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                if (!sc.hasNextLine()) {
                    break;
                }
                String allCommands = sc.nextLine();
                String[] commands = allCommands.trim().split(";");
                for (String command : commands) {
                    execCommand(command);
                    if (isExit) {
                        break;
                    }
                }

                if (isExit) {
                    break;
                }
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void execBatchMode(String[] args) {
        StringBuilder commands = new StringBuilder();
        for (String command : args) {
            commands.append(command + " ");
        }
        String commandsLine = commands.toString();
        String[] commandsArr = commandsLine.trim().split(";");
        for (String command : commandsArr) {
            execCommand(command);
            if (isExit) {
                break;
            }
        }
    }

    private static void execCommand(String command) {
        command = command.trim();
        String[] parts = command.split("\\s+");
        try {
            switch (parts[0]) {
                case "put":
                    if (parts.length == 3) {
                        cache.put(parts[1], parts[2], "db.file");
                    } else {
                        System.out.println("Incorrect number of arguments");
                    }
                    break;
                case "get":
                    if (parts.length == 2) {
                        cache.get(parts[1]);
                    } else {
                        System.out.println("Incorrect number of arguments");
                    }
                    break;
                case "list":
                    if (parts.length == 1) {
                        cache.list("db.file");
                    } else {
                        System.out.println("Incorrect number of arguments");
                    }
                    break;
                case "remove":
                    if (parts.length == 2) {
                        cache.remove(parts[1]);
                    } else {
                        System.out.println("Incorrect number of arguments");
                    }
                    break;
                case "exit":
                    isExit = true;
                    cache.commit();
                    break;
                default:
                    errorUnknownCommand(parts[0]);

            }
        } catch (InvalidCommandException e) {
            System.err.println(e.getMessage());
            if (batchMode) {
                System.exit(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void errorUnknownCommand(String command)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": unknown command");
    }

}
