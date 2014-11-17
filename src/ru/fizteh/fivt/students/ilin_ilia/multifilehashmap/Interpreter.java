package ru.fizteh.fivt.students.theronsg.multifilehashmap;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Interpreter {
    
    public static void interactiveMode(TableList tableList) throws IOException {
            while (true) {
                Scanner scan = new Scanner(System.in);
                System.out.print("$ ");
                String[] str = new String[1];
                try {
                    str[0] = scan.nextLine();
                    if (batchMode(str, tableList, false)) {
                        tableList.exit();
                    }
                } catch (NoSuchElementException e) {
                    System.err.println("There isn't input command");
                    scan.close();
                    System.exit(-1);
                }
            }
    }

    /**
     * @param isBatchMode : if true - batch mode, else interactive
     */
    public static boolean batchMode(final String[] args, TableList tableList, final boolean isBatchMode)
                                        throws NumberFormatException, IOException {
        String arg = "";
        String [] arg1 = null;
        String [] arg2 = null;
        for (int i = 0; i < args.length; i++) {
            arg += (args[i] + " ");
        }
        if (isBatchMode) {
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
                        tableList.put(arg2[1], arg2[2]);
                    } else {
                        if (isBatchMode) {
                            tableList.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "remove":
                    if (checkCommandCorrection("remove", 1, 1, arg2)) {
                        tableList.remove(arg2[1]);
                    } else {
                        if (isBatchMode) {
                            tableList.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "get":
                    if (checkCommandCorrection("get", 1, 1, arg2)) {
                        tableList.get(arg2[1]);
                    } else {
                        if (isBatchMode) {
                            tableList.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "list":
                    if (checkCommandCorrection("get", 1, 0, arg2)) {
                        tableList.list();
                    } else {
                        if (isBatchMode) {
                            tableList.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "create":
                    if (checkCommandCorrection("create", 1, 1, arg2)) {
                        if (tableList.checkNameCorrection(arg2[1])) {
                            tableList.create(arg2[1]);
                        } else {
                            System.err.println("Unacceptable name for the table: " + arg2[1]);
                            if (isBatchMode) {
                                tableList.exit();
                                System.exit(-1);
                            }
                        }
                    } else {
                        if (isBatchMode) {
                            tableList.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "drop":
                    if (checkCommandCorrection("drop", 1, 1, arg2)) {
                        tableList.drop(arg2[1]);
                    } else {
                        if (isBatchMode) {
                            tableList.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "use":
                    if (checkCommandCorrection("use", 1, 1, arg2)) {
                        tableList.use(arg2[1]);
                    } else {
                        if (isBatchMode) {
                            tableList.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "show":
                    if (arg2.length > 1 && arg2[1].equals("tables")) {
                        if (checkCommandCorrection("show tables", 2, 0, arg2)) {
                            tableList.showTables();
                        } else {
                            if (isBatchMode) {
                                tableList.exit();
                                System.exit(-1);
                            }
                        }
                    } else {
                        System.err.println("Unknown command: " + String.join(" ", arg2));
                        if (isBatchMode) {
                            tableList.exit();
                            System.exit(-1);
                        }
                    }
                    break;
                case "exit":
                    if (isBatchMode) {
                        tableList.exit();
                    } else {
                        return true;
                    }
                    break;
                default:
                    System.err.println("Unknown command: " + String.join(" ", arg2));
                    if (isBatchMode) {
                        tableList.exit();
                        System.exit(-1);
                    }
            }
        }
        return false;
    }
    
    public static boolean checkCommandCorrection(final String command, final int commandLength,
                                                final int numberOfArgs, final String [] args) {
        if ((numberOfArgs + commandLength) != args.length) {
             System.err.println(command + ": Incorrect number of arguments: "
             + numberOfArgs + " expected, but " + (args.length - commandLength) + " found.");
             return false;
        }
        return true;
    }
}
