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
            dbPath = Paths.get(dbPath).normalize().toString();
            if (!Paths.get(dbPath).normalize().toFile().exists()) {
                System.err.println("Directory [" + dbPath + "] does not exist.");
                System.exit(ERROR);
            }
            if (!Paths.get(dbPath).normalize().toFile().isDirectory()) {
                System.err.println("Path [" + dbPath + "] is not directory.");
                System.exit(ERROR);
            }
            
            String[] names = Paths.get(dbPath).toFile().list();
            try {
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
                                    mfmParser(buffer);
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
                                mfmParser(buffer);
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
    
    private static void mfmCreate(final String[] args) throws Exception {
        if (args.length != 2) {
            mfmWrongQuantity("create");
        }
        if (args[1].isEmpty()) {
            mfmWrongInput("create");
        }
        if (Paths.get(dbPath, args[1]).normalize().toFile().exists()) {
            if (Paths.get(dbPath, args[1]).normalize().toFile().isDirectory()) {
                System.out.println(args[1] + " exists");
            } else {
                mfmTableNameIsFile("create", args[1]);
            }
        } else {
            if (Paths.get(dbPath, args[1]).normalize().toFile().mkdir()) {
                System.out.println("created");
            } else {
                mfmNotMkdir("create", args[1]);
            }
        }
        dbInformation.put(Paths.get(dbPath, args[1]).normalize().getFileName().toString(), 0);
    }

    private static void mfmDrop(final String[] args) throws Exception {
        if (args.length != 2) {
            mfmWrongQuantity("drop");
        }
        if (args[1].isEmpty()) {
            mfmWrongInput("drop");
        }
        try {
            Path pathToFile = Paths.get(dbPath, args[1]).normalize();
            if (!pathToFile.toFile().exists()) {
                System.out.println("tablename not exists");
                return;
            }
            if (pathToFile.equals(dbTable.getPath())) {
                dbTable = null;
            }
            if (pathToFile.toFile().isDirectory()) {
                String[] names = pathToFile.toFile().list();
                if (names.length != 0) {
                    for (String name : names) {
                        if (Paths.get(pathToFile.toString(), name).normalize().toFile().isDirectory()) {
                            System.setProperty("user.dir", pathToFile.toString());
                            String[] helpArray = new String[] {"drop", name};
                            mfmDrop(helpArray);
                            System.setProperty("user.dir", pathToFile.getParent().toString());
                        }
                        if (Paths.get(pathToFile.toString(), name).normalize().toFile().isFile()) {
                            if (!Paths.get(pathToFile.toString(), name).normalize().toFile().delete()) {
                                mfmSmthWrong("drop");
                            }
                        }
                    }
                }
                if (!pathToFile.toFile().delete()) {
                    mfmSmthWrong("drop");
                }
            } else {
                mfmNotDirectory("drop", args[1]);
            }
        } catch (InvalidPathException invException) {
            mfmInvalidName("drop", args[2]);
        } catch (SecurityException secException) {
            mfmSecurity("drop", args[2]);
        }
        dbInformation.remove(Paths.get(dbPath, args[1]).normalize().getFileName().toString());
        System.out.println("dropped");
    }

    private static void mfmUse(final String[] args) throws Exception {
        if (args.length != 2) {
            mfmWrongQuantity("use");
        }
        if (args[1].isEmpty()) {
            mfmWrongInput("use");
        }
        
        if (!Paths.get(dbPath, args[1]).normalize().toFile().exists()) {
            System.out.println(args[1] + " not exists");
        } else {
            if (dbTable != null) {
                dbTable.close();
            }
            dbTable = new DataBase(Paths.get(dbPath, args[1]).normalize().toString());
            System.out.println("using " + args[1]);
        }
    }

    private static void mfmShowTables(final String args[]) throws Exception {
        if (args.length != 2) {
            mfmWrongInput("show tables");
        }
        if (!args[1].equals("tables")) {
            mfmWrongInput("show tables");
        }
        
       Set<String> tables = dbInformation.keySet();
       System.out.println("table_name row_count");
       for (String table : tables) {
           if (dbTable != null) {
               if (table.equals(dbTable.getPath())) {
                   System.out.println(table + " " + dbTable.list().length);
               } else {
                   System.out.println(table + " " + dbInformation.get(table));
               }
           }
       }
    }
    
    private static void mfmExit(final String[] args) throws Exception {
        if (args.length != 1) {
            mfmWrongQuantity("exit");
        }
        if (dbTable != null) {
            dbTable.close();
        }
        System.exit(SUCCESS);
    }

    private static void mfmPut(final String[] args) throws Exception {
        if (args.length != 3) {
            mfmWrongQuantity("put");
        }
        if ((args[1].isEmpty()) || (args[2].isEmpty())) {
            mfmWrongInput("put");
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
        dbTable.writeOnDisk();
    }
    
    private static void mfmGet(final String[] args) throws Exception {
        if (args.length != 2) {
            mfmWrongQuantity("get");
        }
        if (args[1].isEmpty()) {
            mfmWrongInput("get");
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

    private static void mfmRemove(final String[] args) throws Exception {
        if (args.length != 2) {
            mfmWrongQuantity("remove");
        }
        if (args[1].isEmpty()) {
            mfmWrongInput("remove");
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
        dbTable.writeOnDisk();
    }
    
    private static void mfmList(final String[] args) throws Exception {
        if (args.length != 1) {
            mfmWrongQuantity("list");
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

    private static void mfmParser(final String[] args) throws Exception {
        switch (args[0]) {
            case "create":
                mfmCreate(args);
                break;
            case "drop":
                mfmDrop(args);
                break;
            case "use":
                mfmUse(args);
                break;
            case "show":
                mfmShowTables(args);
                break;
            case "exit":
                mfmExit(args);
                break;
                
            case "put":
                mfmPut(args);
                break;
            case "get":
                mfmGet(args);
                break;
            case "remove":
                mfmRemove(args);
                break;
            case "list":
                mfmList(args);
                break;
            default:
                System.err.println("Command does not exist: [" + args[0] + "]");
        }
    }
    
    private static void mfmInvalidName(final String commandName, final String arg) throws Exception {
        throw new Exception(commandName + ": [" + arg + "] is invalid name.");
    }
    
    private static void mfmNotDirectory(final String commandName,  final String arg) throws Exception {
        throw new Exception(commandName + ": [" + arg + "] is not a directory.");
    }
    
    private static void mfmNotMkdir(final String commandName, final String arg) throws Exception {
        throw new Exception(commandName + ": failed to create a directory [" + arg + "].");
    }
    private static void mfmSecurity(final String commandName, final String arg) throws Exception {
        throw new Exception(commandName + ": access to the [" + arg + "] is prohibeted.");
    }
    
    private static void mfmSmthWrong(final String commandName) throws Exception {
        throw new Exception(commandName + "something went wrong.");
    }
    
    private static void mfmTableNameIsFile(final String commandName, final String arg) throws Exception {
        throw new Exception(commandName + ": [" + arg + "] is file");
    }
    
    private static void mfmWrongQuantity(final String commandName) throws Exception {
        throw new Exception(commandName + ": wrong quantity of arguments.");
    }

    private static void mfmWrongInput(final String commandName) throws Exception {
        throw new Exception(commandName + ": wrong input.");
    }
}
