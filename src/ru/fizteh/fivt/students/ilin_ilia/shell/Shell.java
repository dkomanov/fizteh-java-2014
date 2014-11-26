package ru.fiztech.fivt.students.theronsg.shell;

import ru.fiztech.fivt.students.theronsg.shell.commands.Cat;
import ru.fiztech.fivt.students.theronsg.shell.commands.Ls;
import ru.fiztech.fivt.students.theronsg.shell.commands.MkDir;
import ru.fiztech.fivt.students.theronsg.shell.commands.Pwd;
import ru.fiztech.fivt.students.theronsg.shell.commands.Rm;
import ru.fiztech.fivt.students.theronsg.shell.commands.Cd;
import ru.fiztech.fivt.students.theronsg.shell.commands.Mv;
import ru.fiztech.fivt.students.theronsg.shell.commands.Cp;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Shell {
    public static void interactiveMode() {
        String [] commands = {"ls", "cat", "rm", "cd",
                "cp", "exit", "mkdir", "mv", "pwd"};
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String str = "";
            try {
            str = scan.next();
            } catch (NoSuchElementException e) {
                System.exit(-1);
            }
            String fileName;
            switch (str) {
                case "ls":
                    Ls.run();
                    break;
                case "cat":
                    Cat.run(scan.next());
                    break;
                case "rm":
                    fileName = scan.next();
                    if (fileName.equals("-r")) {
                        Rm.run(scan.next(), true);
                    } else {
                        Rm.run(fileName, false);
                    }
                    break;
                case "cd":
                    try {
                        fileName = scan.next();
                        for (String s: commands) {
                            if (s.equals(fileName)) {
                                Cd.run("");
                            }
                        }
                        Cd.run(fileName);
                    } catch (NoSuchElementException ae) {
                        Cd.run("");
                    }
                    break;
                case "cp":
                    fileName = scan.next();
                    if (fileName.equals("-r")) {
                        Cp.run(scan.next(), scan.next(), true);
                    } else {
                        Cp.run(fileName, scan.next(), false);
                    }
                    break;
                case "exit":
                    System.exit(0);
                    break;
                case "mkdir":
                    MkDir.run(scan.next());
                    break;
                case "mv":
                    Mv.run(scan.next(), scan.next());
                    break;
                case "pwd":
                    Pwd.run();
                    break;
                default:
                    System.err.println("Unknown command:" + str);
                    System.exit(-1);
            }
        }
    }

    public static void commandMode(final String[] args) {
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
            case "ls":
                Ls.run();
                break;
            case "cat":
                if (arg2.length == 1) {
                    System.out.println("Cat doesn't have argument");
                    System.exit(-1);
                } else {
                    Cat.run(arg2[1]);
                }
                break;
            case "rm":
                if (arg2.length == 1) {
                    System.out.println("Rm doesn't have argument");
                    System.exit(-1);
                } else {
                    if (arg2[1].equals("-r")) {
                        if (arg2.length == 2) {
                            System.out.println("Rm doesn't have argument");
                            System.exit(-1);
                        } else {
                            Rm.run(arg2[2], true);
                        }
                    } else {
                        Rm.run(arg2[1], false);
                    }
                }
                break;
            case "cd":
                if (arg2.length == 1) {
                    Cd.run("");
                } else {
                    Cd.run(arg2[1]);
                }
                break;
            case "cp":
                if (arg2.length == 1) {
                    System.out.println("Cp doesn't have arguments");
                    System.exit(-1);
                } else {
                    if (arg2[1].equals("-r")) {
                        if (arg2.length == 2 || arg2.length == 3) {
                            System.out.println("Cp doesn't have arguments");
                            System.exit(-1);
                        } else {
                            Cp.run(arg2[2], arg2[3], true);
                        }
                    } else {
                        if (arg2.length == 2) {
                            System.out.println("Cat doesn't have argument");
                            System.exit(-1);
                        } else {
                            Cp.run(arg2[1], arg2[2], false);
                        }
                    }
                }
                break;
            case "exit":
                System.exit(0);
                break;
            case "mkdir":
                if (arg2.length == 1) {
                    System.out.println("MkDir doesn't have argument");
                    System.exit(-1);
                }
                MkDir.run(arg2[1]);
                break;
            case "mv":
                if (arg2.length == 1 || arg2.length == 2) {
                    System.out.println("Mv doesn't have arguments");
                    System.exit(-1);
                } else {
                    Mv.run(arg2[1], arg2[2]);
                }
                break;
            case "pwd":
                Pwd.run();
                break;
            default:
                System.err.println("Unknown command:" + arg2[0]);
                System.exit(-1);
            }
        }
    }
}
