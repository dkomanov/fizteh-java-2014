package ru.fizteh.fivt.students.theronsg.multifilehashmap;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Interpreter {
    
    public static void interactiveMode(TableList tL) throws IOException {
            while (true) {
                Scanner scan = new Scanner(System.in);
                System.out.print("$ ");
                String str = "";
                try {
                    str = scan.next();
                } catch (NoSuchElementException e) {
                    System.err.println("There isn't input command");
                    System.exit(-1);
                }
                switch (str) {
                    case "put":
                        try {
                            tL.put(scan.next(), scan.next());
                        } catch (NoSuchElementException e) {
                            System.err.println("Miss command");
                            System.exit(-1);
                        }
                        break;
                    case "remove":
                        try {
                            tL.remove(scan.next());
                        } catch (NoSuchElementException e) {
                            System.err.println("Miss command");
                            System.exit(-1);
                        }
                        break;
                    case "get":
                        try {
                            tL.get(scan.next());
                        } catch (NoSuchElementException e) {
                            System.err.println("Miss command");
                            System.exit(-1);
                        }
                        break;
                    case "list":
                        tL.list();
                        break;
                    case "create":
                        tL.create(scan.next());
                        break;
                    case "drop":
                        tL.drop(scan.next());
                        break;
                    case "use":
                        tL.use(scan.next());
                        break;
                    case "show":
                        String next = scan.next();
                        if (next.equals("tables")) {
                            tL.showTables();
                        } else {
                            System.err.println("Unknown command:" + str + " " + next);
                            System.exit(-1);
                        }
                        break;
                    case "exit":
                        scan.close();
                        tL.exit();
                        break;
                    default:
                        System.err.println("Unknown command:" + str);
                        System.exit(-1);
                }
            }
    }

    public static void batchMode(final String[] args, TableList tL) throws NumberFormatException, IOException {
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
                        System.err.println("Put doesn't have arguments");
                        System.exit(-1);
                    } else {
                        tL.put(arg2[1], arg2[2]);
                    }
                    break;
                case "remove":
                    if (arg2.length == 1) {
                        System.err.println("Remove doesn't have arguments");
                        System.exit(-1);
                    } else {
                        tL.remove(arg2[1]);
                    }
                    break;
                case "get":
                    if (arg2.length == 1) {
                        System.err.println("Get doesn't have arguments");
                        System.exit(-1);
                    } else {
                        tL.get(arg2[1]);
                    }
                    break;
                case "list":
                    tL.list();
                    break;
                case "create":
                    if (arg2.length == 1) {
                        System.err.println("Create doesn't have arguments");
                        System.exit(-1);
                    } else {
                        tL.create(arg2[1]);
                    }
                    break;
                case "drop":
                    if (arg2.length == 1) {
                        System.err.println("Drop doesn't have arguments");
                        System.exit(-1);
                    } else {
                        tL.drop(arg2[1]);
                    }
                       break;
                case "use":
                    if (arg2.length == 1) {
                        System.err.println("Use doesn't have arguments");
                        System.exit(-1);
                    } else {
                        tL.use(arg2[1]);
                    }
                       break;
                case "show":
                    if (arg2.length == 1) {
                        System.err.println("Unknown command:" + arg2[0]);
                        System.exit(-1);
                    } else {
                        if (arg2[1].equals("tables")) {
                            tL.showTables();
                        } else {
                            System.err.println("Unknown command:" + arg2[0] + " " + arg2[1]);
                            System.exit(-1);
                        }
                    }
                     break;
                case "exit":
                    tL.exit();
                    break;
                default:
                    System.err.println("Unknown command:" + arg2[0]);
                    System.exit(-1);
            }
        }
    }

}
