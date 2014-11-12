package ru.fizteh.fivt.students.SergeyAksenov.MultiFileHashMap;

import java.io.File;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

//
public class Executor {
    public static boolean checkArgNumber(int from, int value, int to) {
        return from <= value && value <= to;
    }

    public static void execute(final HashMap<String, Command> commandMap,
                               final String[] commands, DataBase dataBase)
            throws MultiFileMapException {
        if (commands[0].equals("")) {
            return;
        }
        Command cmd = commandMap.get(commands[0]);
        if (cmd == null) {
            System.out.println("unknown command");
            return;
        }
        commandMap.get(commands[0]).run(commands, dataBase);
    }

    public static void execLine(String line,
                                final HashMap<String, Command> commandMap,
                                DataBase dataBase) {
        String[] commands = line.trim().split(";");
        try {
            for (String command : commands) {
                command = command.trim();
                String[] splittedCommand = command.split("\\s+");
                execute(commandMap, splittedCommand, dataBase);
            }
        } catch (MultiFileMapException e) {
            System.exit(0);
        }
    }

    public static void interactiveMode(
            final HashMap<String, Command> commandMap, DataBase dataBase)
            throws MultiFileMapException {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                if (!scanner.hasNextLine()) {
                    System.exit(0);
                }
                String command = scanner.nextLine();
                execLine(command, commandMap, dataBase);
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void delete(File fileToRem) {
        if (fileToRem.isDirectory()) {
            File[] files = fileToRem.listFiles();
            if (files == null) {
                System.out.println("Cannot delete file " + fileToRem.toString());
            }
            for (File file : files) {
                delete(file);
            }
        }
        if (!fileToRem.delete()) {
            System.out.println("Cannot delete file " + fileToRem.toString());
        }

    }
}