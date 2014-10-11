package ru.fizteh.fivt.students.andrewzhernov.filemap;

import java.util.Scanner;

public class FileMap {
    public static void main(String[] args) {
        try {
            DataBase dataBase = new DataBase(System.getProperty("db.file"));
            if (args.length == 0) {
                interactiveMode(dataBase);
            } else {
                packageMode(args, dataBase);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void interactiveMode(DataBase dataBase) {
        Scanner input = null;
        try {
            input = new Scanner(System.in);
            System.out.print("$ ");
            while (input.hasNextLine()) {
                try {
                    executeCommand(parseCommand(input.nextLine()), dataBase);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                System.out.print("$ ");
            }
            dataBase.writeInFile();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            System.out.println();
            input.close(); 
        }
    }

    public static void packageMode(String[] args, DataBase dataBase) {
        int isError = 0;
        try {
            String[] input = parseInput(args);
            for (String cmd : input) {
                executeCommand(parseCommand(cmd), dataBase);
            }
            dataBase.writeInFile();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            isError = 1;
        }
        System.exit(isError);
    }

    private static String[] parseInput(String[] args) {
        StringBuilder input = new StringBuilder();
        for (String cmd : args) {
            input.append(cmd).append(' ');
        }
        return input.toString().split("\\s*;\\s*");
    }

    private static String[] parseCommand(String cmd) {
        return cmd.split("\\s+");
    }

    public static void executeCommand(String[] cmds, DataBase dataBase) throws Exception {
        if (cmds.length > 0 && cmds[0].length() > 0) {
            if (cmds[0].equals("put")) {
                dataBase.put(cmds[1], cmds[2]);
            } else if (cmds[0].equals("get")) {
                dataBase.get(cmds[1]);
            } else if (cmds[0].equals("remove")) {
                dataBase.remove(cmds[1]);
            } else if (cmds[0].equals("list")) {
                dataBase.list();
            } else if (cmds[0].equals("exit")) {
                dataBase.writeInFile();
                System.exit(0);
            } else {
                throw new Exception(cmds[0] + ": command not found");
            }
        }
    }
}
