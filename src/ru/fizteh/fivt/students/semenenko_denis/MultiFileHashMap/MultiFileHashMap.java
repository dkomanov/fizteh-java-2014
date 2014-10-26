package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by denny_000 on 11.10.2014.
 */
public class MultiFileHashMap {
    private static boolean batchMode;
    private static boolean isExit;
    private static DataBaseCache cache = new DataBaseCache();
    private static boolean workWithTableMode = false;
    private static TableHash usingTable;

    public static void main(String[] args) {
        try {
            Path dbPath = Paths.get(System.getProperty("fizteh.db.dir"));
            File dataBaseDirectory = dbPath.toFile();
            if (!dataBaseDirectory.isDirectory() || !dataBaseDirectory.exists()) {
                System.err.println("Root is not directory or not exists");
                System.exit(-1);
            }
            cache.init(dbPath);
            if (args.length == 0) {
                batchMode = false;
                execInteractiveMode();
            } else {
                batchMode = true;
                execBatchMode(args);
            }
        } catch (NullPointerException e) {
            System.err.println("No parameter: root of database.");
            System.exit(-1);
        } catch (WorkWithMemoryException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
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
        cache.commit();
    }

    private static void execCommand(String command) {
        command = command.trim();
        String[] parts = command.split("\\s+");
        try {
            switch (parts[0]) {
                case "create":
                    if (parts.length == 2) {
                        cache.createTable(parts[1]);
                    } else {
                        errorCntArguments("create");
                    }
                    break;
                case "drop" :
                    if (parts.length == 2) {
                        if (workWithTableMode && usingTable.getTableName().equals(parts[1]))
                        cache.dropTable(parts[1]);
                        workWithTableMode = false;
                        usingTable = null;
                    } else {
                        errorCntArguments("drop");
                    }
                    break;
                case "use" :
                    if (parts.length == 2) {
                        TableHash newTable = cache.useTable(parts[1]);
                        if (newTable != null) {
                            workWithTableMode = true;
                            usingTable = newTable;
                        }
                    } else {
                        errorCntArguments("use");
                    }
                    break;
                case "show" :
                    if (parts.length == 2) {
                        cache.showTables();
                    } else {
                        errorCntArguments("show");
                    }
                    break;
                case "put":
                    if (!workWithTableMode) {
                        System.out.println("no table");
                        break;
                    }
                    if (parts.length == 3) {
                        cache.put(parts[1], parts[2], usingTable);
                    } else {
                        errorCntArguments("put");
                    }
                    break;
                case "get":
                    if (!workWithTableMode) {
                        System.out.println("no table");
                        break;
                    }
                    if (parts.length == 2) {
                        cache.get(parts[1], usingTable);
                    } else {
                        errorCntArguments("get");
                    }
                    break;
                case "list":
                    if (!workWithTableMode) {
                        System.out.println("no table");
                        break;
                    }
                    if (parts.length == 1) {
                        cache.list(usingTable);
                    } else {
                        errorCntArguments("list");
                    }
                    break;
                case "remove":
                    if (!workWithTableMode) {
                        System.out.println("no table");
                        break;
                    }
                    if (parts.length == 2) {
                        cache.remove(parts[1], usingTable);
                    } else {
                        errorCntArguments("remove");
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
        } catch (DatabaseFileStructureException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void errorCntArguments(String command)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": invalid number of arguments.");
    }

    private static void errorUnknownCommand(String command)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": unknown command");
    }

}


