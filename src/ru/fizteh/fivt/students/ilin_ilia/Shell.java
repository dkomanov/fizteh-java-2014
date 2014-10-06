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
    public static void interactiveMode () {
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
        for (int i = 0; i < args.length; i++) {
        	int sep = args[i].indexOf(";");
        	String com = "";
        	if (sep != -1) {
        		com = args[i].substring(0, sep);
        	} else {
        		com = args[i];
        	}
            switch (com) {
            case "ls":
                Ls.run();
            case "cat":
                i++;
                if (i == args.length) {
                    System.out.println("Cat doesn't have argument");
                    System.exit(-1);
                } else {
                    Cat.run(args[i]);
                }
            case "rm":
                i++;
                if (i == args.length) {
                    System.out.println("Rm doesn't have argument");
                    System.exit(-1);
                } else {
                    if (args[i].equals("-r")) {
                        i++;
                        if (i == args.length) {
                            System.out.println("Rm doesn't have argument");
                            System.exit(-1);
                        } else {
                            Rm.run(args[i], true);
                        }
                    } else {
                        Rm.run(args[i], false);
                    }
                }
            case "cd":
                ;
            case "cp":
                i++;
                if (i == args.length) {
                    System.out.println("Cp doesn't have arguments");
                    System.exit(-1);
                } else {
                    if (args[i].equals("-r")) {
                        i++;
                        if (i == args.length || i + 1 == args.length) {
                            System.out.println("Cp doesn't have arguments");
                            System.exit(-1);
                        } else {
                            Cp.run(args[i], args[i + 1], true);
                        }
                    } else {
                        i++;
                        if (i == args.length) {
                            System.out.println("Cat doesn't have argument");
                            System.exit(-1);
                        } else {
                         Cp.run(args [i - 1], args[i], false);
                        }
                    }
                }
            case "exit":
                System.exit(0);
            case "mkdir":
                i++;
                if (i == args.length) {
                    System.out.println("MkDir doesn't have argument");
                    System.exit(-1);
                }
                MkDir.run(args[i]);
            case "mv":
                i++;
                if (i == args.length || i + 1 == args.length) {
                    System.out.println("Mv doesn't have arguments");
                    System.exit(-1);
                } else {
                    Mv.run(args[i], args[i + 1]);
                    i++;
                }
            case "pwd":
                Pwd.run();
            default:
                System.err.println("Unknown command:" + args[i]);
                System.exit(-1);
            }
        }
    }

}
