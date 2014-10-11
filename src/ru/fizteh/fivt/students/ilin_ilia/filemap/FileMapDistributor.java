package ru.fiztech.fivt.students.theronsg.filemap;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class FileMapDistributor {

    public static void interactiveMode() {
        Scanner scan = new Scanner(System.in);
        FileMap db = new FileMap(System.getProperty("db.file"));
        while (true) {
            System.out.println("$ ");
            String str = "";
            try {
                str = scan.next();
            } catch (NoSuchElementException e) {
                System.out.println("There isn't input command");
                System.exit(-1);
            }
            String com;
            switch (str) {
                case "put":
                    try {
                        db.put(scan.next(), scan.next());
                    } catch (NoSuchElementException e) {
                        System.out.println("Miss command");
                        System.exit(-1);
                    }
                    break;
                case "remove":
                    try {
                        db.remove(scan.next());
                    } catch (NoSuchElementException e) {
                        System.out.println("Miss command");
                        System.exit(-1);
                    }
                    break;
                case "get":
                    try {
                        db.get(scan.next());
                    } catch (NoSuchElementException e) {
                        System.out.println("Miss command");
                        System.exit(-1);
                    }
                    break;
                case "list":
                    db.list();
                    break;
                case "exit":
                    db.exit();
                    break;
                default:
                    System.err.println("Unknown command:" + str);
                    System.exit(-1);
            }
        }
    }

    public static void commandMode(final String[] args) {
        FileMap db = new FileMap(System.getProperty("db.file"));
        String arg = "";
        String [] arg1 = null;
        String [] arg2 = null;
        for (int i = 0; i < args.length; i++) {
            arg += (args[i] + " ");
        }
        arg += ";exit";
        String ss = arg.trim();
        arg1 = ss.split(";");
        for (int i = 0; i < arg1.length; i++) {
            ss = arg1[i].trim();
            arg2 = ss.split(" ");
            switch (arg2[0]) {
                case "put":
                    if (arg2.length == 1 || arg2.length == 2) {
                        System.out.println("Put doesn't have arguments");
                        System.exit(-1);
                    } else {
                        db.put(arg2[1], arg2[2]);
                    }
                    break;
                case "remove":
                    if (arg2.length == 1) {
                        System.out.println("Remove doesn't have arguments");
                        System.exit(-1);
                    } else {
                        db.remove(arg2[1]);
                    }
                    break;
                case "get":
                    if (arg2.length == 1) {
                        System.out.println("Get doesn't have arguments");
                        System.exit(-1);
                    } else {
                        db.get(arg2[1]);
                    }
                    break;
                case "list":
                    db.list();
                    break;
                case "exit":
                    db.exit();
                    break;
                default:
                System.err.println("Unknown command:" + arg2[0]);
                System.exit(-1);
            }
        }
    }

}
