package ru.fizteh.fivt.students.theronsg.multifilehashmap;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Interpreter {
    
    public static void interactiveMode(TableList tL) throws IOException {
            while (true) {
                Scanner scan = new Scanner(System.in);
                System.out.print("$ ");
                String[] str = new String[1];
                try {
                    str[0] = scan.nextLine();
                    if (batchMode(str, tL, false)) {
                        tL.exit();
                    }
                } catch (NoSuchElementException e) {
                    System.err.println("There isn't input command");
                    scan.close();
                    System.exit(-1);
                }
            }
    }

    public static boolean batchMode(final String[] args, TableList tL, final boolean flag)
                                        throws NumberFormatException, IOException {
        String arg = "";
        String [] arg1 = null;
        String [] arg2 = null;
        for (int i = 0; i < args.length; i++) {
            arg += (args[i] + " ");
        }
        if (flag) {
            arg += ";exit";
        }
        String ss = arg.trim();
        arg1 = ss.split(";");
        for (int i = 0; i < arg1.length; i++) {
            ss = arg1[i].trim();
            arg2 = ss.split(" ");
            switch (arg2[0]) {
                case "put":
                    if (checkCommandCorrection("put", 1, 2, arg2)) {
                        tL.put(arg2[1], arg2[2]);
                    } else {
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "remove":
                    if (checkCommandCorrection("remove", 1, 1, arg2)) {
                        tL.remove(arg2[1]);
                    } else {
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "get":
                    if (checkCommandCorrection("get", 1, 1, arg2)) {
                        tL.get(arg2[1]);
                    } else {
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "list":
                    if (checkCommandCorrection("get", 1, 0, arg2)) {
                        tL.list();
                    } else {
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "create":
                    if (checkCommandCorrection("create", 1, 1, arg2)) {
                        if (tL.checkNameCorrection(arg2[1])) {
                            tL.create(arg2[1]);
                        } else {
                            System.err.println("Unacceptable name for the table: " + arg2[1]);
                            if (flag) {
                                tL.exit();
                                System.exit(-1);
                            }
                        }
                    } else {
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "drop":
                    if (checkCommandCorrection("drop", 1, 1, arg2)) {
                        tL.drop(arg2[1]);
                    } else {
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "use":
                    if (checkCommandCorrection("use", 1, 1, arg2)) {
                        tL.use(arg2[1]);
                    } else {
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "show":
                    if (arg2.length > 1 && arg2[1].equals("tables")) {
                        if (checkCommandCorrection("show tables", 2, 0, arg2)) {
                            tL.showTables();
                        } else {
                            if (flag) {
                                tL.exit();
                                System.exit(-1);
                            }
                        }
                    } else {
                        System.err.println("Unknown command: " + String.join(" ", arg2));
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "exit":
                    if (flag) {
                        tL.exit();
                    } else {
                        return true;
                    }
                    break;
                default:
                    System.err.println("Unknown command: " + String.join(" ", arg2));
                    if (flag) {
                        tL.exit();
                        System.exit(-1);
                    }
            }
        }
        return false;
    }
    
    public static boolean checkCommandCorrection(final String command, int commandLength,  int numberOfArgs, final String [] args) {
        if ((numberOfArgs + commandLength) != args.length) {
             System.err.println(command + ": Incorrect number of arguments: "
             + numberOfArgs + " expected, but " + (args.length - commandLength) + " found.");
             return false;
        }
        return true;
    }
}
