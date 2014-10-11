package ru.fizteh.fivt.students.artem_gritsay.FileMap;

import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.nio.file.*;



public class FileMap {
    private static HashMap<String,String> filemap;
    private static Path PathFile;
    private static void exit(Path PathtoFile, HashMap<String, String> filemap)  {
        try (RandomAccessFile dataBaseFile
                = new RandomAccessFile(PathtoFile.toString(), "rw")) {
            dataTrance.putD(dataBaseFile, filemap);
        } catch (Exception e) {
            System.err.println("Cannot writing to file");
        }
    }
    private static void getLine(String com) throws ShellexitException, IOException {
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
                    throw new ShellexitException();
                default:
                    System.out.println("Invalid comannd");
            }
    }

    private static void switchCommands(String[] s) throws ShellexitException {
        try {
            for (String command : s) {
                getLine(command);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(0);
        }
    }

    private static void parsLine(String[] args) throws ShellexitException {
        StringBuilder com = new StringBuilder();
        for (String arg : args) {
            com.append(arg);
            com.append(' ');
        }
        switchLine(com.toString());
    }

    private static void switchLine(String line) throws ShellexitException {
        String[] commands = line.trim().split(";");
        switchCommands(commands);
    }
    private static void interactive() throws ShellexitException {
            Scanner scan = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                String commands = scan.nextLine();
                switchLine(commands);
            }

    }
    private static void put(String[] args) {
        System.out.println("oops");
        if(args.length==3) {
            System.out.println("oops");
            String V = filemap.put(args[1],args[2]);
            System.out.println("oops");
            if(V!=null) {
                System.out.println("overwrite");
                System.out.println(V);
            } else {
                System.out.println("new");
            }
        } else {
            System.out.println("Incorrect arguments");
        }
    }
    private static void get(String[] args) {
        if(args.length==2) {
            String V = filemap.get(args[1]);
            if(V!=null) {
                System.out.println("found");
                System.out.println(V);
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.println("Incorrect arguments");
        }
    }
    private static void list(String[] args) {
        if(args.length==1) {
            Set<String> keys = filemap.keySet();
            Integer i=0;
            for(String key : keys) {
                System.out.print(key);
                i++;
                if(i<keys.size()) {
                    System.out.print(", ");
                } else {
                    System.out.println();
                }
            }
        } else {
            System.out.println("Incorrect arguments");
        }
    }
    private static void remove(String[] args) {
        if(args.length==2) {
            String s = filemap.remove(args[1]);
            if(s!=null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.println("Incorrect arguments");
        }
    }
    private static void makefile() {
        filemap = new HashMap<>();
        try {
            PathFile = Paths.get(System.getProperty("db.file"));
            try (RandomAccessFile dataBaseFile
                         = new RandomAccessFile(PathFile.toString(), "r")) {
                if (dataBaseFile.length() > 0) {
                    dataTrance.getNewData(filemap, dataBaseFile);
                }
            } catch (FileNotFoundException e) {
                PathFile.toFile().createNewFile();
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
        } catch (ShellexitException t) {
            exit(PathFile, filemap);
            System.exit(0);
        }

    }

}
class ShellexitException extends Exception {}


