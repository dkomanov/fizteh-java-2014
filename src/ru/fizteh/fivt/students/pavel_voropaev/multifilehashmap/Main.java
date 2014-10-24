package ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.commands.*;

public class Main {
    private static String dbPath;

    public static void main(String[] args) {
        Database database = null;
        dbPath = System.getProperty("fizteh.db.dir");
        if (dbPath == null) {
            System.err.println("You must specify fizteh.db.dir");
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
                batchMode(args, database);
            } catch (Exception e) {
                System.err.print(e.getMessage());
                System.exit(1);
            }
        } else {
            try {
                interactiveMode(database);
            } catch (Exception e) {
                System.err.print(e.getMessage());
                System.exit(1);
            }
        }
    }

    private static void batchMode(String[] args, Database db) throws Exception {
        StringBuilder commandsLine = new StringBuilder();
        for (String arg : args) {
            if (!arg.equals(" ")) {
                commandsLine.append(arg);
                commandsLine.append(' ');
            }
        }

        String[] command = commandsLine.toString().split(";");
        for (String comm : command) {
            execCommand(comm.trim(), db);
        }
        execCommand("exit", db);
    }

    private static void interactiveMode(Database db) 
            throws IllegalStateException {
        Scanner in = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.print("$ ");
            try {
                String line = in.nextLine();
                exit = executeLine(line, db);
            } catch (NoSuchElementException e) {
                break;
            }
        }
        in.close();
    }
    
    private static boolean executeLine(String line, Database db) {
        boolean exit = false;
        try {
            exit = execCommand(line, db);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } 
        return exit;
    }
    

    private static boolean execCommand(String p, Database db) 
            throws IllegalArgumentException, IllegalStateException, IOException {
        String[] command = p.split("\\s+");
        if (command[0].equals("")) {
            return false;
        }
        if (command[0].equals("put")) {
            Put.exec(db.getWorkingTable(), command);
            return false;
        }
        if (command[0].equals("get")) {
            Get.exec(db.getWorkingTable(), command);
            return false;
        }
        if (command[0].equals("remove")) {
            Remove.exec(db.getWorkingTable(), command);
            return false;
        }
        if (command[0].equals("list")) {
            List.exec(db.getWorkingTable(), command);
            return false;
        }
        if (command[0].equals("create")) {
            Create.exec(db, command);
            return false;
        }
        if (command[0].equals("drop")) {
            Drop.exec(db, command);
            return false;
        }
        if (command[0].equals("show") && command.length > 1 && command[1].equals("tables")) {
            ShowTables.exec(db, command);
            return false;
        }
        if (command[0].equals("use")) {
            Use.exec(db, command);
            return false;
        }
        if (command[0].equals("exit")) {
            Exit.exec(db, command);
            return true;
        }

        throw new IllegalArgumentException("Unknown command: " + p);
    }
    
}
