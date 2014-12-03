package ru.fizteh.fivt.students.SukhanovZhenya.Junit;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    private static MultiFileHashMap multiFileHashMap = null;

    public static void main(String[] args) {
        multiFileHashMap = new MultiFileHashMap(System.getProperty("fizteh.db.dir"));

        if (args.length > 0) {
            interactiveWork(args);
        }

        System.out.print(System.getProperty("user.name") + " :");
        Scanner in = new Scanner(System.in);
        do {
            String commands = null;
            try {
                commands = in.nextLine();
            } catch (NoSuchElementException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
            assert commands != null;
            for (String command : commands.split(" *;+( *;*)*")) {
                doCommand(command);
            }
            System.out.print(System.getProperty("user.name") + " :");
        } while (in.hasNextLine());
    }

    private static void interactiveWork(String[] args) {
        for (String command : String.join(" ", args).split(" *;+( *;*)*")) {
            doCommand(command);
        }
        multiFileHashMap.exit();
    }

    private static void doCommand(String command) {
        String[] job = command.split(" +");
        switch (job[0]) {
            case "put":
                if (job.length != 3) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                multiFileHashMap.put(job[1], job[2]);
                break;
            case "get":
                if (job.length != 2) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                multiFileHashMap.get(job[1]);
                break;
            case "size":
                if (job.length > 1) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                System.out.println(multiFileHashMap.size());
                break;
            case "commit":
                if (job.length > 1) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                multiFileHashMap.commit();
                break;
            case "rollback":
                if (job.length > 1) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                multiFileHashMap.rollback();
                break;
            case "remove":
                if (job.length != 2) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                multiFileHashMap.remove(job[1]);
                break;
            case "list":
                if (job.length > 1) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                multiFileHashMap.list();
                break;
            case "create":
                if (job.length != 2) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                multiFileHashMap.create(job[1]);
                break;
            case "drop":
                if (job.length != 2) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                multiFileHashMap.drop(job[1]);
                break;
            case "use":
                if (job.length != 2) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                multiFileHashMap.use(job[1]);

                break;
            case "show":
                if (job.length != 2 && !job[1].equals("tables")) {
                    System.err.println("Incorrect command");
                    break;
                }
                multiFileHashMap.showTables();
                break;
            case "exit":
                if (job.length > 1) {
                    System.err.println("Incorrect arguments");
                    break;
                }
                multiFileHashMap.exit();
                break;
            case "":
                break;
            default:
                System.err.println("Incorrect command");
                break;
        }
    }


}
