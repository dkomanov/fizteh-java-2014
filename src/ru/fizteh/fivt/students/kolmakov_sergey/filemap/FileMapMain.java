package ru.fizteh.fivt.students.kolmakov_sergey.filemap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.util.*;

public class FileMapMain {

    private static Path filePath;
    private static TreeMap<String, String> fileMap;
    private static int exitStatus;
    private static boolean packageMode;

    private static boolean checkArguments(int from, int value, int to) {
        return from <= value && value <= to;
    }

    private static void put(String[] args){
        if (!checkArguments(3, args.length, 3)) {
            throw new IllegalArgumentException("put: invalid number of arguments");
        }
        String saver = fileMap.put(args[1], args[2]);
        if (saver != null) {
            System.out.println("overwrite");
            System.out.println(saver);
        } else {
            System.out.println("new");
        }
    }

    private static void get(String[] args){
        if (!checkArguments(2, args.length, 2)) {
            throw new IllegalArgumentException("get: invalid number of arguments");
        }
        String saver = fileMap.get(args[1]);
        if (saver != null) {
            System.out.println("found");
            System.out.println(saver);
        } else {
            System.out.println("not found");
        }
    }

    private static void remove(String[] args){
        if (!checkArguments(2, args.length, 2)) {
            throw new IllegalArgumentException("remove: invalid number of arguments");
        }
        String saver = fileMap.remove(args[1]);
        if (saver != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }

    private static void list(String[] args){
        if (!checkArguments(1, args.length, 1)) {
            throw new IllegalArgumentException("list: invalid number of arguments");
        }
        Set<String> keys = fileMap.keySet();
        int counter = 0;
        for (String current : keys) {
            ++counter;
            System.out.print(current);
            if (counter != keys.size()){
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    private static void connect() throws FileMapExitException {
        fileMap = new TreeMap<>();
        try {
            filePath = Paths.get(System.getProperty("db.file"));
            try (RandomAccessFile dataBaseFile
                         = new RandomAccessFile(filePath.toString(), "r")) {
                if (dataBaseFile.length() > 0) {
                    Communicator.getData(dataBaseFile, fileMap);
                }
            } catch (FileNotFoundException e) {
                filePath.toFile().createNewFile();
            }
        } catch (Exception e) {
            System.err.println("Can't find or create data base file");
            exitStatus = -1;
            throw new FileMapExitException();
        }
    }

    private static void exit(Path filePath, TreeMap<String, String> fileMap) {
        try (RandomAccessFile dataBaseFile
                     = new RandomAccessFile(filePath.toString(), "rw")) {
            Communicator.putData(dataBaseFile, fileMap);
        } catch (Exception e) {
            System.err.println("Error writing to file");
        }
    }

    private static void execute(String[] command) throws FileMapExitException {
        try {
            if (command.length > 0 && !command[0].isEmpty()) {
                switch(command[0]) {
                    case "put":
                        put(command);
                        break;
                    case "get":
                        get(command);
                        break;
                    case "remove":
                        remove(command);
                        break;
                    case "list":
                        list(command);
                        break;
                    case "exit":
                        if (command.length > 1) {
                            exitStatus = -1;
                            System.out.println("exit: invalid number of arguments");
                        } else{
                            exitStatus = 0;
                        }
                        throw new FileMapExitException();
                    default:
                        throw new
                                IllegalArgumentException("Invalid command");
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            if (packageMode) {
                exitStatus = -1;
                throw new FileMapExitException();
            }
        }
    }

    private static void runPackage(String[] args) throws FileMapExitException {
        packageMode = true;
        StringBuilder builder = new StringBuilder();
        for (String current : args) {
            builder.append(current);
            builder.append(" ");
        }
        String[] commandLine = builder.toString().split(";");
        for (String current : commandLine) {
            execute(current.trim().split("\\s+"));
        }
    }

    private static void runInteractive() throws FileMapExitException {
        packageMode = false;
        String[] commandLine;
        try (Scanner scan = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                commandLine = scan.nextLine().trim().split(";");
                for (String current : commandLine) {
                    execute(current.trim().split("\\s+"));
                }
            }
        } catch (NoSuchElementException e) {
            exitStatus = -1;
            throw new FileMapExitException();
        }
    }

    public static void main(String[] args) {
        try {
            connect();
            if (args.length == 0) {
                runInteractive();
            } else {
                runPackage(args);
            }
        } catch (FileMapExitException t) {
            exit(filePath, fileMap);
            System.exit(exitStatus);
        }
    }
}

class FileMapExitException extends Exception {
}
