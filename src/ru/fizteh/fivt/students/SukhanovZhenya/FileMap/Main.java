package ru.fizteh.fivt.students.SukhanovZhenya.FileMap;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static FileMap fileMap = new FileMap(System.getProperty("db.file"));

    public static void main(String[] args) {
        fileMap.getFile();

        if (args.length > 0) {
            packFileMap(args);
        }

        System.out.print(System.getProperty("user.name") + " :");
        Scanner in = new Scanner(System.in);
        do {
            String commands = null;
            try {
                commands = in.nextLine();
            } catch (NoSuchElementException e) {
                System.err.println(e.getMessage());
                fileMap.exit();
            }
            assert commands != null;
            for (String command : commands.split(" *;+( *;*)*")) {
                doCommand(command);
            }
            System.out.print(System.getProperty("user.name") + " :");
        } while (in.hasNextLine());
    }

    private static void doCommand(String command) {
        String[] job = command.split(" +");
        if (job[0].equals("")) {
            return;
        }
        switch (job[0]) {
            case "put":
                if (job.length != 3) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                fileMap.add(job[1], job[2]);
                fileMap.appendFile();
                break;
            case "get":
                if (job.length != 2) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                fileMap.get(job[1]);
                break;
            case "remove":
                if (job.length != 2) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                fileMap.remove(job[1]);
                fileMap.appendFile();
                break;
            case "list":
                if (job.length > 1) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                fileMap.list();
                break;
            case "exit":
                if (job.length > 1) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                fileMap.exit();
                break;
            case "":
                break;
            default:
                System.err.println("Incorrect command");
                break;
        }
    }

    private static void packFileMap(String[] args) {
        for (String command : String.join(" ", args).split(" *;+( *;*)*")) {
            doCommand(command);
        }
        fileMap.exit();
    }
}

