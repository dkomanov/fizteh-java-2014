package ru.fizteh.fivt.students.Volodin_Denis.FileMap;

import java.util.Scanner;

public class FileMap {
    private static final int SUCCESS = 0;
    private static final int ERROR = 1;

    private static String dbPath;
    private static DataBase database;

    public static void main(final String[] args) {
        try {
            dbPath = System.getProperty("db.file");
            if (dbPath == null) {
                System.err.println("Path was not found.");
                System.exit(ERROR);
            }
            database = new DataBase(dbPath);
            if (args.length == 0) {
                // Interactive mode.
                Scanner scanner = new Scanner(System.in);
                try {
                    do {
                        System.out.print("$ ");
                        String[] filemapIn = scanner.nextLine().split(";");
                        for (int i = 0; i < filemapIn.length; ++i) {
                            if (filemapIn[i].length() > 0) {
                                String[] buffer = filemapIn[i].trim().split("\\s+");
                                try {
                                    filemapParser(buffer);
                                } catch (Exception except) {
                                    System.err.println(except.getMessage());
                                }
                            }
                        }
                    } while(true);
                } catch (Exception except) {
                    System.err.println("\nSmth wrong.");
                    scanner.close();
                    System.exit(ERROR);
                }
                scanner.close();
            } else {
                try {
                    StringBuilder helpArray = new StringBuilder();
                    for (int i = 0; i < args.length; ++i) {
                        helpArray.append(args[i]).append(' ');
                    }
                    String longStr = helpArray.toString();
                    String[] filemapIn = longStr.split(";");
                    for (int i = 0; i < filemapIn.length; ++i) {
                        if (filemapIn[i].length() > 0) {
                            String[] buffer = filemapIn[i].trim().split("\\s+");
                            try {
                                filemapParser(buffer);
                            } catch (Exception except) {
                                System.err.println(except.getMessage());
                            }
                        }
                    }
                } catch (Exception except) {
                    System.err.println("\nSmth wrong.");
                    System.exit(ERROR);
                }
            }
        } catch (Exception except) {
            System.err.println(except.getMessage());
            System.exit(ERROR);
        }
        System.exit(SUCCESS);
    }

    //
    // Basic commands.
    //
    // Begin.
    //

    private static void filemapPut(final String[] args) throws Exception {
        if (args.length != 3) {
            filemapWrongQuantity("put");
        }
        if ((args[1].isEmpty()) || (args[2].isEmpty())) {
            filemapWrongInput("put");
        }

        String value = database.search(args[1]);
        if (value == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(value);
        }
        database.put(args[1], args[2]);
        database.writeOnDisk();
    }

    private static void filemapGet(final String[] args) throws Exception {
        if (args.length != 2) {
            filemapWrongQuantity("get");
        }
        if (args[1].isEmpty()) {
            filemapWrongInput("get");
        }

        String value = database.search(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }

    private static void filemapRemove(final String[] args) throws Exception {
        if (args.length != 2) {
            filemapWrongQuantity("remove");
        }
        if (args[1].isEmpty()) {
            filemapWrongInput("remove");
        }

        String value = database.search(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            database.remove(args[1]);
            System.out.println("removed");
        }
        database.writeOnDisk();
    }

    private static void filemapList(final String[] args) throws Exception {
        if (args.length != 1) {
            filemapWrongQuantity("list");
        }

        String[] keys = database.list();
        if (keys.length == 0) {
            System.out.println("");
        } else {
            for (int i = 0; i + 1 < keys.length; ++i) {
                System.out.print(keys[i] + ", ");
            }
            System.out.println(keys[keys.length - 1]);
        }
    }

    private static void filemapExit(final String[] args) throws Exception {
        if (args.length != 1) {
            filemapWrongQuantity("exit");
        }

        System.exit(SUCCESS);
    }

    //
    // Basic commands.
    //
    // End.
    //

    private static void filemapParser(final String[] args) throws Exception {
        switch (args[0]) {
            case "put":
                filemapPut(args);
                break;
            case "get":
                filemapGet(args);
                break;
            case "remove":
                filemapRemove(args);
                break;
            case "list":
                filemapList(args);
                break;
            case "exit":
                filemapExit(args);
                break;
            default:
                System.err.println("Command does not exist: [" + args[0] + "]");
        }
    }

    private static void filemapWrongQuantity(final String commandName) throws Exception {
        throw new Exception(commandName + ": wrong quantity of arguments.");
    }

    private static void filemapWrongInput(final String commandName) throws Exception {
        throw new Exception(commandName + ": wrong input.");
    }
}
