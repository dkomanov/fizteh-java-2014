package ru.fizteh.fivt.students.pavel_voropaev.filemap;

import java.util.Scanner;

public class Main {
    private static String dbPath;
    private static Database database;

    public static void main(String[] args) {
        dbPath = System.getProperty("db.file");
        if (dbPath == null) {
            System.err.println(dbPath + ": no such database file");
            System.exit(1);
        }
        try {
            database = new Database(dbPath);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            System.exit(1);
        }

        if (args.length > 0) {
            try {
                packageMode(args);
            } catch (Exception e) {
                System.err.print(e.getMessage());
                System.exit(1);
            }
        } else {
            try {
                interactiveMode();
            } catch (Exception e) {
                System.err.print(e.getMessage());
                System.exit(1);
            }
        }
    }

    private static void packageMode(String[] args) throws Exception {
        StringBuilder commandsLine = new StringBuilder();
        for (String arg : args) {
            if (!arg.equals(" ")) {
                commandsLine.append(arg);
                commandsLine.append(' ');
            }
        }

        String[] command = commandsLine.toString().split(";");
        for (String comm : command) {
            execCommand(comm.trim());
        }

    }

    private static void interactiveMode() {
        boolean exit = false;
        Scanner scan = new Scanner(System.in);
        System.out.print("$ ");
        String nextLine = scan.nextLine();
        while (!exit) {
            try {
                exit = execCommand(nextLine.trim());
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            } catch (IllegalStateException e) {
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            } finally {
                if (!exit) {
                    System.out.print("$ ");
                    nextLine = scan.nextLine();
                }
            }
        }
        scan.close();
    }

    private static boolean execCommand(String p)
            throws Exception {
        String[] command = p.split("\\s+");
        if (command[0].equals("")) {
            return false;
        }
        if (command[0].equals("put")) {
            Put.exec(database, command);
            return false;
        }
        if (command[0].equals("get")) {
            Get.exec(database, command);
            return false;
        }
        if (command[0].equals("remove")) {
            Remove.exec(database, command);
            return false;
        }
        if (command[0].equals("list")) {
            List.exec(database, command);
            return false;
        }
        if (command[0].equals("exit")) {
            if (command.length != 1) {
                ThrowExc.tooManyArg("exit", "Usage: exit");
            }
            database.write();
            return true;
        }

        throw new IllegalArgumentException("Unknown command: " + p);
    }

}
