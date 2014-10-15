
package ru.fizteh.fivt.students.artem_gritsay.FileMap;


import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.nio.file.*;



public class FileMap {
    private static HashMap<String, String> filemap;
    private static Path pathFile;
    private static void exit(Path pathtoFile, HashMap<String, String> filemap)  {
        try (RandomAccessFile dataBaseFile
                = new RandomAccessFile(pathtoFile.toString(), "rw")) {
            DataCommunicator.putData(dataBaseFile, filemap);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static boolean checkarguments(String[] args, Integer k) {
        if (args.length == k) {
            return true;
        } else {
            System.out.println("Incorrect arguments");
            return false;
        }
    }

    private static void getLine(String com) throws ShellExitException, IOException {
        com = com.trim();
        String[] s = com.split("\\s+");
            switch (s[0]) {
                case "put":
                    put(s);
                    break;
                case "get":
                    get(s);
                    break;
                case "remove":
                    remove(s);
                    break;
                case "list":
                    list(s);
                    break;
                case "exit":
                    throw new ShellExitException();
                default:
                    System.err.println("Invalid command");
            }
    }

    private static void switchCommands(String[] s) throws ShellExitException {
        try {
            for (String command : s) {
                getLine(command);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void parsLine(String[] args) throws ShellExitException {
        StringBuilder com = new StringBuilder();
        for (String arg : args) {
            com.append(arg);
            com.append(' ');
        }
        switchLine(com.toString());
        throw new ShellExitException();
    }

    private static void switchLine(String line) throws ShellExitException {
        String[] commands = line.trim().split(";");
        switchCommands(commands);
    }

    private static void interactive() throws ShellExitException {
            Scanner scan = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                String commands = scan.nextLine();
                switchLine(commands);
            }

    }

    private static void put(String[] args) {
        if (checkarguments(args, 3)) {
            String v = filemap.put(args[1], args[2]);
            if (v != null) {
                System.out.println("overwrite");
                System.out.println(v);
            } else {
                System.out.println("new");
            }
        }
    }

    private static void get(String[] args) {
        if (checkarguments(args, 2)) {
            String v = filemap.get(args[1]);
            if (v != null) {
                System.out.println("found");
                System.out.println(v);
            } else {
                System.out.println("not found");
            }
        }
    }

    private static void list(String[] args) {
        if (checkarguments(args, 1)) {
            Set<String> keys = filemap.keySet();
            Integer i = 0;
            for (String key : keys) {
                System.out.print(key);
                i++;
                if (i < keys.size()) {
                    System.out.print(", ");
                } else {
                    System.out.println();
                }
            }
        }
    }

    private static void remove(String[] args) {
        if (checkarguments(args, 2)) {
            String s = filemap.remove(args[1]);
            if (s != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
    }

    private static void makefile() {
        filemap = new HashMap<>();
        try {
            pathFile = Paths.get(System.getProperty("db.file"));
            try (RandomAccessFile dataBaseFile
                         = new RandomAccessFile(pathFile.toString(), "r")) {
                if (dataBaseFile.length() > 0) {
                    DataCommunicator.getNewData(dataBaseFile, filemap);
                }
            } catch (FileNotFoundException e) {
                pathFile.toFile().createNewFile();
            }
        } catch (Exception e) {
            System.err.println("Can't find or create data base file");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        try {
            makefile();
            if (args.length == 0) {
                interactive();
            } else {
                parsLine(args);
            }
        } catch (ShellExitException t) {
            exit(pathFile, filemap);
            System.exit(0);
        }

    }

}
class ShellExitException extends Exception {}


