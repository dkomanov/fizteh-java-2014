package ru.fizteh.fivt.students.RadimZulkaneev.shell;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public final class InputParse {
    private InputParse() {
    }
    public static void myParse(final String[] arg) {
        String currentDir = MyFunctions.myPwd();
        ArrayList<String> current = new ArrayList<String>();
        for (int i = 0; i < arg.length; ++i) {
            current.clear();
            while (i < arg.length) {
                if (!(arg[i].indexOf(";") >= 0)) {
                    current.add(arg[i]);
                    i++;
                } else {
                    current.add(arg[i].substring(0, arg[i].indexOf(";")));
                    break;
                }
            }
            
            switch (current.get(0)) {
            case "ls":
                InputParse.assertInput("ls", current.size());
                MyFunctions.myLs(currentDir);
                break;
            
            case "pwd":
                InputParse.assertInput("pwd", current.size());
                System.out.println(currentDir);
                break;
            case "cat":
                InputParse.assertInput("cat", current.size());
                if (current.size() < 2) {
                    System.out.println("cat: Check input");
                    System.exit(1);
                }
                if (!MyFunctions.myCat(currentDir, current.get(1))) {
                    System.exit(1);
                }
                break;
            case "cd":
                InputParse.assertInput("cd", current.size());
                if (current.size() < 2) {
                    System.out.println("cd: Check input");
                    System.exit(1);
                }
                try {
                    currentDir = MyFunctions.myCd(currentDir, 
                            current.get(1), true);
                } catch (FileNotFoundException ex) {
                    System.exit(1);
                }
                break;
            case "mkdir":
                InputParse.assertInput("mkdir", current.size());
                if (current.size() < 2) {
                    System.out.println("mkdir: Check input");
                    System.exit(1);
                }
                if (!MyFunctions.myMkdir(currentDir, current.get(1))) {
                    System.exit(1);
                }
                break;
            case "exit":
                System.exit(0);
                break;
            case "rm":
                InputParse.assertInput("rm", current.size());
                if (current.size() < 2) {
                    System.out.println("rm: incorrect input");
                    System.exit(1);
                    break;
                }
                if (current.get(1).equals("[-r]") 
                        || current.get(1).equals("-r")) {
                    if (current.size() < 3) {
                        System.out.println("rm: incorrect input");
                        System.exit(1);
                        break;
                    }
                    if (!MyFunctions.myRmDir(currentDir, current.get(2))) {
                        System.exit(1);
                    }
                } else {
                    if (!MyFunctions.myRmFile(currentDir, current.get(1))) {
                        System.exit(1);
                    }
                }
                break;
            case "mv":
                InputParse.assertInput("mv", current.size());
                if (current.size()  < 3) {
                    System.out.println("mv: Incorrect input");
                    System.exit(1);
                }
                if (!MyFunctions.myMv(currentDir, current.get(1), 
                        current.get(2))) {
                    System.exit(1);
                }
                break;
            case "cp":
                InputParse.assertInput("cp", current.size());
                try {
                    if (current.get(1).equals("-r")) {            
                        if (-1 == MyFunctions.myCpDir(currentDir,
                                current.get(2), current.get(3), "cp")) {
                            System.exit(1);
                        }
                    } else {
                        InputParse.assertInput("cp1", current.size());
                        if (!MyFunctions.myCpFile(currentDir, current.get(1), 
                                current.get(2), "cp")) {
                            System.exit(1);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.out.println("cp: No input");
                    System.exit(1);
                } catch (IndexOutOfBoundsException ex) {
                    System.out.println("cp: No input");
                    System.exit(1);
                }

                break;
            default:
                System.out.println("Incorrect input " + current.get(0));
                System.exit(1);
                break;
            }
        }
    }
    private static void assertInput(String command, int size) {
        switch (command) {
        case "cd":
            if (size > 2) {
                System.out.println(command + ": too much arguments");
                System.exit(1);
            }
            break;
        case "pwd":
            if (size > 1) {
                System.out.println(command + ": too much arguments");
                System.exit(1);
            }
            break;
        case "ls":
            if (size > 1) {
                System.out.println(command + ": too much arguments");
                System.exit(1);
            }
            break;
        case "mv":
            if (size > 3) {
                System.out.println(command + ": too much arguments");
                System.exit(1);
            }
            break;
        case "cp": // copy Directory
            if (size > 4) {
                System.out.println(command + ": too much arguments");
                System.exit(1);
            }
            break;
        case "cp1": // copy File
            if (size > 3) {
                System.out.println(command + ": too much arguments");
                System.exit(1);
            }
            break;
        case "cat":
            if (size > 2) {
                System.out.println(command + ": too much arguments");
                System.exit(1);
            }
            break;
        case "mkdir":
            if (size > 2) {
                System.out.println(command + ": too much arguments");
                System.exit(1);
            }
            break;
        case "":
            break;
        default: 
            break;
        }
    }
}
