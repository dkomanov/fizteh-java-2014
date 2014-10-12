package ru.fizteh.fivt.students.andrewzhernov.filemap;

import java.util.Scanner;

public class FileMap {
    public static void main(String[] args) {
        try {
            DataBase dataBase = new DataBase(System.getProperty("db.file"));
            if (args.length == 0) {
                interactiveMode(dataBase);
            } else {
                batchMode(args, dataBase);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void interactiveMode(DataBase dataBase) throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.print("$ ");
        while (input.hasNextLine()) {
            try {
                executeCommand(parseCommand(input.nextLine()), dataBase);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            System.out.print("$ ");
        }
        dataBase.saveToDisk();
        input.close(); 
    }

    public static void batchMode(String[] args, DataBase dataBase) throws Exception {
        String[] input = parseInput(args);
        for (String cmd : input) {
            executeCommand(parseCommand(cmd), dataBase);
        }
        dataBase.saveToDisk();
    }

    private static String[] parseInput(String[] args) throws Exception {
        StringBuilder input = new StringBuilder();
        for (String cmd : args) {
            input.append(cmd).append(' ');
        }
        return input.toString().split("\\s*;\\s*");
    }

    private static String[] parseCommand(String cmd) throws Exception {
        return cmd.trim().split("\\s+");
    }

    public static void executeCommand(String[] cmd, DataBase dataBase) throws Exception {
        if (cmd.length > 0 && cmd[0].length() > 0) {
            if (cmd[0].equals("put")) {
                if (cmd.length != 3) {
                    throw new Exception("Usage: put <key> <value>");
                }
                dataBase.put(cmd[1], cmd[2]);
            } else if (cmd[0].equals("get")) {
                if (cmd.length != 2) {
                    throw new Exception("Usage: get <key>");
                }
                dataBase.get(cmd[1]);
            } else if (cmd[0].equals("remove")) {
                if (cmd.length != 2) {
                    throw new Exception("Usage: remove <key>");
                }
                dataBase.remove(cmd[1]);
            } else if (cmd[0].equals("list")) {
                if (cmd.length != 1) {
                    throw new Exception("Usage: list");
                }
                dataBase.list();
            } else if (cmd[0].equals("exit")) {
                if (cmd.length != 1) {
                    throw new Exception("Usage: exit");
                }
                try {
                    dataBase.saveToDisk();
                    System.exit(0);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            } else {
                throw new Exception(cmd[0] + ": no such command");
            }
        }
    }
}
