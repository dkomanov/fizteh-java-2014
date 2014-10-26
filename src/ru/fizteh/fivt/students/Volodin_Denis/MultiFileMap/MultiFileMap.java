package ru.fizteh.fivt.students.Volodin_Denis.MultiFileMap;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class MultiFileMap {
    private static final int SUCCESS = 0;
    private static final int ERROR = 1;
    
    private static String dbPath;
    private static DataBase dbTable;
    private static HashMap<String, Integer> dbInformation; 
    
    public static void main(final String[] args) {    
        try {
            dbPath = System.getProperty("fizteh.db.dir");
            if (dbPath == null) {
                System.err.println("Path was not found.");
                System.exit(ERROR);
            }
            if (!Paths.get(dbPath).isAbsolute()) {
                dbPath = Paths.get(dbPath).toAbsolutePath().normalize().toString();
            }
            if (!Paths.get(dbPath).normalize().toFile().exists()) {
                if (Paths.get(dbPath).normalize().getParent().toFile().exists()) {
                    Files.createDirectory(Paths.get(dbPath).normalize().getFileName());
                } else {
                    System.err.println("Directory [" + Paths.get(dbPath).normalize().getParent().getFileName() + "] does not exist.");
                    System.exit(ERROR);
                }
            }
            if (!Paths.get(dbPath).normalize().toFile().isDirectory()) {
                System.err.println("Path [" + dbPath + "] is not directory.");
                System.exit(ERROR);
            }
            
            String[] names = Paths.get(dbPath).toFile().list();
            try {
                dbInformation = new HashMap<String, Integer>();
                for (String name : names) {
                    if (Paths.get(dbPath, name).toFile().isFile()) {
                        Files.delete(Paths.get(dbPath, name));
                    }
                    if (Paths.get(dbPath, name).toFile().isDirectory()) {
                        DataBase temp = new DataBase(Paths.get(dbPath, name).normalize().toString());
                        dbInformation.put(name, temp.list().length);
                        temp.close();
                    }
                }
            } catch (Exception exception) {
                System.err.println(exception.getMessage());
                System.exit(ERROR);
            }
            
            dbTable = null;
            if (args.length == 0) {
                // Interactive mode.
                Scanner scanner = new Scanner(System.in);
                try {
                    do {
                        System.out.print("$ ");
                        String[] input = scanner.nextLine().split(";");
                        for (int i = 0; i < input.length; ++i) {
                            if (input[i].length() > 0) {
                                String[] buffer = input[i].trim().split("\\s+");
                                try {
                                    parser(buffer);
                                } catch (Exception exception) {
                                    System.err.println(exception.getMessage());
                                }
                            }
                        }
                    } while(true);
                } catch (Exception exception) {
                    System.err.println("Smth wrong.");
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
                    String[] input = longStr.split(";");
                    for (int i = 0; i < input.length; ++i) {
                        if (input[i].length() > 0) {
                            String[] buffer = input[i].trim().split("\\s+");
                            try {
                                parser(buffer);
                            } catch (Exception exception) {
                                System.err.println(exception.getMessage());
                            }
                        }
                    }
                } catch (Exception exception) {
                    System.err.println("Smth wrong.");
                    System.exit(ERROR);
                }
            }
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            System.exit(ERROR);
        }
        System.exit(SUCCESS);
    }

    //
    // Basic commands.
    //
    // Begin.
    //
    
    private static void create(final String[] args) throws Exception {
        if (args.length != 2) {
            wrongQuantity("create");
        }
        if (args[1].isEmpty()) {
            wrongInput("create");
        }
        if (Paths.get(dbPath, args[1]).normalize().toFile().exists()) {
            if (Paths.get(dbPath, args[1]).normalize().toFile().isDirectory()) {
                System.out.println(args[1] + " exists");
            } else {
                tableNameIsFile("create", args[1]);
            }
        } else {
            if (Paths.get(dbPath, args[1]).normalize().toFile().mkdir()) {
                System.out.println("created");
            } else {
                notMkdir("create", args[1]);
            }
        }
        dbInformation.put(Paths.get(dbPath, args[1]).normalize().getFileName().toString(), 0);
    }

    private static void drop(final String[] args) throws Exception {
        if (args.length != 2) {
            wrongQuantity("drop");
        }
        if (args[1].isEmpty()) {
            wrongInput("drop");
        }
        try {
            Path pathToFile = Paths.get(dbPath, args[1]).normalize();
            if (!pathToFile.toFile().exists()) {
                System.out.println("tablename not exists");
                return;
            }
            if (dbTable != null) {
                if (pathToFile.getFileName().toString().equals(dbTable.getPath())) {
                    dbTable.close();
                    dbTable = null;
                }
            }
            
            if (!pathToFile.toFile().isDirectory()) {
                notDirectory("drop", args[1]);
            }
            recursiveDrop(pathToFile);
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
         
        dbInformation.remove(Paths.get(dbPath, args[1]).normalize().getFileName().toString());
        System.out.println("dropped");
    }

    private static void recursiveDrop(final Path pathToFile) throws Exception {
        try {
            if (!pathToFile.toFile().exists()) {
                notExists("drop", pathToFile.toString());
                return;
            }
            if (pathToFile.toFile().isFile()) {
                if (!pathToFile.toFile().delete()) {
                    smthWrong("drop");
                }
            }
            if (pathToFile.toFile().isDirectory()) {
                String[] names = pathToFile.toFile().list();
                if (names.length != 0) {
                    for (String name : names) {
                        if (Paths.get(pathToFile.toString(), name).normalize().toFile().isDirectory()) {
                            recursiveDrop(Paths.get(pathToFile.toString(), name).normalize());
                        }
                        if (Paths.get(pathToFile.toString(), name).normalize().toFile().isFile()) {
                            if (!Paths.get(pathToFile.toString(), name).normalize().toFile().delete()) {
                                smthWrong("drop");
                            }
                        }
                    }
                }
                if (!pathToFile.toFile().delete()) {
                    smthWrong("drop");
                }
            }
        } catch (InvalidPathException invException) {
            invalidName("drop", invException.getMessage());
        } catch (SecurityException secException) {
            security("drop", secException.getMessage());
        }
    }
    
    private static void use(final String[] args) throws Exception {
        if (args.length != 2) {
            wrongQuantity("use");
        }
        if (args[1].isEmpty()) {
            wrongInput("use");
        }
        
        if (!Paths.get(dbPath, args[1]).normalize().toFile().exists()) {
            System.out.println(args[1] + " not exists");
        } else {
            if (dbTable != null) {
                if (dbTable.getPath().equals(Paths.get(dbPath, args[1]).normalize().getFileName().toString())) {
                    System.out.println("is already in use");
                    return;
                }
                dbInformation.remove(dbTable.getPath());
                dbInformation.put(dbTable.getPath(), dbTable.list().length);
                dbTable.close();
            }
            dbTable = new DataBase(Paths.get(dbPath, args[1]).normalize().toString());
            System.out.println("using " + args[1]);
        }
    }

    private static void showTables(final String[] args) throws Exception {
        if (args.length != 2) {
            wrongInput("show tables");
        }
        if (!args[1].equals("tables")) {
            wrongInput("show tables");
        }

        Set<String> tables = dbInformation.keySet();
        if (!tables.isEmpty()) {
            System.out.println("table_name row_count");
        }
        for (String table : tables) {
            if (dbTable != null) {
                if (table.equals(dbTable.getPath())) {
                    System.out.println(table + " " + dbTable.list().length);
                } else {
                    System.out.println(table + " " + dbInformation.get(table));
                }
            } else {
                System.out.println(table + " " + dbInformation.get(table));
            }
        }
    }
    
    private static void exit(final String[] args) throws Exception {
        if (args.length != 1) {
            wrongQuantity("exit");
        }
        if (dbTable != null) {
            dbTable.close();
        }
        System.exit(SUCCESS);
    }

    private static void put(final String[] args) throws Exception {
        if (args.length != 3) {
            wrongQuantity("put");
        }
        if ((args[1].isEmpty()) || (args[2].isEmpty())) {
            wrongInput("put");
        }
        if (dbTable == null) {
            System.out.println("no table");
            return;
        }
        
        String value = dbTable.get(args[1]);
        if (value == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(value);
        }
        dbTable.put(args[1], args[2]);
    }
    
    private static void get(final String[] args) throws Exception {
        if (args.length != 2) {
            wrongQuantity("get");
        }
        if (args[1].isEmpty()) {
            wrongInput("get");
        }
        if (dbTable == null) {
            System.out.println("no table");
            return;
        }
        
        String value = dbTable.get(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }

    private static void remove(final String[] args) throws Exception {
        if (args.length != 2) {
            wrongQuantity("remove");
        }
        if (args[1].isEmpty()) {
            wrongInput("remove");
        }
        if (dbTable == null) {
            System.out.println("no table");
            return;
        }
        
        String value = dbTable.get(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            dbTable.remove(args[1]);
            System.out.println("removed");
        }
    }
    
    private static void list(final String[] args) throws Exception {
        if (args.length != 1) {
            wrongQuantity("list");
        }
        
        if (dbTable == null) {
            System.out.println("no table");
            return;
        }
        
        String[] keys = dbTable.list();
        if (keys.length == 0) {
            System.out.println("");
        } else {
            for (int i = 0; i + 1 < keys.length; ++i) {
                System.out.print(keys[i] + ", ");
            }
            System.out.println(keys[keys.length - 1]);
        }
    }
    
    //
    // Basic commands.
    //
    // End.
    //

    private static void parser(final String[] args) throws Exception {
        switch (args[0]) {
            case "create":
                create(args);
                break;
            case "drop":
                drop(args);
                break;
            case "use":
                use(args);
                break;
            case "show":
                showTables(args);
                break;
            case "exit":
                exit(args);
                break;
                
            case "put":
                put(args);
                break;
            case "get":
                get(args);
                break;
            case "remove":
                remove(args);
                break;
            case "list":
                list(args);
                break;
            default:
                System.err.println("Command does not exist: [" + args[0] + "]");
        }
    }
    
    private static void invalidName(final String commandName, final String arg) throws Exception {
        throw new Exception(commandName + ": [" + arg + "] is invalid name.");
    }
    
    private static void notDirectory(final String commandName,  final String arg) throws Exception {
        throw new Exception(commandName + ": [" + arg + "] is not a directory.");
    }
    
    private static void notMkdir(final String commandName, final String arg) throws Exception {
        throw new Exception(commandName + ": failed to create a directory [" + arg + "].");
    }
    
    private static void notExists(final String commandName, final String arg) throws Exception {
        throw new Exception(commandName + ": [" + arg + "] does not exists.");
    }
    
    private static void security(final String commandName, final String arg) throws Exception {
        throw new Exception(commandName + ": access to the [" + arg + "] is prohibeted.");
    }
    
    private static void smthWrong(final String commandName) throws Exception {
        throw new Exception(commandName + "something went wrong.");
    }
    
    private static void tableNameIsFile(final String commandName, final String arg) throws Exception {
        throw new Exception(commandName + ": [" + arg + "] is file");
    }
    
    private static void wrongQuantity(final String commandName) throws Exception {
        throw new Exception(commandName + ": wrong quantity of arguments.");
    }

    private static void wrongInput(final String commandName) throws Exception {
        throw new Exception(commandName + ": wrong input.");
    }
}
