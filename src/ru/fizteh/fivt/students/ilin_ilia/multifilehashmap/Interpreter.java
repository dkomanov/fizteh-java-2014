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

    public static boolean batchMode(final String[] args, TableList tL, boolean flag) throws NumberFormatException, IOException {
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
                    if (arg2.length == 1 || arg2.length == 2) {
                        System.err.println("\"put\" doesn't enough have arguments");
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else if (arg2.length > 3) {
                        if (arg2.length == 4) {
                            System.err.println("\"put\" has surplus argument");
                        }
                        if (arg2.length > 4) {
                            System.err.println("\"put\" has surplus arguments");
                        }
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else {
                        tL.put(arg2[1], arg2[2]);
                    }
                    break;
                case "remove":
                    if (arg2.length == 1) {
                        System.err.println("\"remove\" doesn't enough have arguments");
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else if (arg2.length > 2) {
                        if (arg2.length == 3) {
                            System.err.println("\"remove\" has surplus argument");
                        }
                        if (arg2.length > 3) {
                            System.err.println("\"remove\" has surplus arguments");
                        }
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else {
                        tL.remove(arg2[1]);
                    }
                    break;
                case "get":
                    if (arg2.length == 1) {
                        System.err.println("\"get\" doesn't have arguments");
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else if (arg2.length > 2) {
                        if (arg2.length == 3) {
                            System.err.println("\"get\" has surplus argument");
                        }
                        if (arg2.length > 3) {
                            System.err.println("\"get\" has surplus arguments");
                        }
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else {
                        tL.get(arg2[1]);
                    }
                    break;
                case "list":
                    if (arg2.length == 2) {
                        System.err.println("\"list\" has surplus argument");
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    }
                    if (arg2.length > 2) {
                        System.err.println("\"list\" has surplus arguments");
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    }
                    tL.list();
                    break;
                case "create":
                    if (arg2.length == 1) {
                        System.err.println("\"create\" doesn't have arguments");
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else if (arg2.length > 2) {
                        if (arg2.length == 3) {
                            System.err.println("\"create\" has surplus argument");
                        }
                        if (arg2.length > 3) {
                            System.err.println("\"create\" has surplus arguments");
                        }
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else {
                        if (tL.checkNameCorrection(arg2[1])) {
                            tL.create(arg2[1]);
                        } else {
                            System.err.println("Unacceptable name for the table: " + arg2[1]);
                            if (flag) {
                                tL.exit();
                                System.exit(-1);
                            }
                        }
                    }
                    break;
                case "drop":
                    if (arg2.length == 1) {
                        System.err.println("\"drop\" doesn't have arguments");
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else if (arg2.length > 2) {
                        if (arg2.length == 3) {
                            System.err.println("\"drop\" has surplus argument");
                        }
                        if (arg2.length > 3) {
                            System.err.println("\"drop\" has surplus arguments");
                        }
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else {
                        tL.drop(arg2[1]);
                    }
                       break;
                case "use":
                    if (arg2.length == 1) {
                        System.err.println("\"use\" doesn't have arguments");
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else if (arg2.length > 2) {
                        if (arg2.length == 3) {
                            System.err.println("\"use\" has surplus argument");
                        }
                        if (arg2.length > 3) {
                            System.err.println("\"use\" has surplus arguments");
                        }
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else {
                        tL.use(arg2[1]);
                    }
                       break;
                case "show":
                    if (arg2.length == 1) {
                        System.err.println("Unknown command: " + arg2[0]);
                        if (flag) {
                            tL.exit();
                            System.exit(-1);
                        }
                    } else {
                        if (arg2.length == 2) {
                            if (arg2[1].equals("tables")) {
                                tL.showTables();
                            } else {
                                System.err.println("Unknown command: " + arg2[0] + " " + arg2[1]);
                                if (flag) {
                                    tL.exit();
                                    System.exit(-1);
                                }
                            }
                        } else {
                            if (arg2.length == 3) {
                                if (arg2[1].equals("tables")) {
                                    System.err.println("\"show tables\" has surplus argument");
                                } else {
                                    System.err.print("Unknown command: ");
                                    for (String s: arg2) {
                                        System.err.print(s + " ");
                                    }
                                    System.out.println();
                                }
                            }
                            if (arg2.length > 3) {
                                if (arg2[1].equals("tables")) {
                                    System.err.println("\"show tables\" has surplus argument");
                                } else {
                                    System.err.print("Unknown command: ");
                                    for (String s: arg2) {
                                        System.err.print(s + " ");
                                    }
                                    System.out.println();
                                }
                            }
                            if (flag) {
                                tL.exit();
                                System.exit(-1);
                            }
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

}
