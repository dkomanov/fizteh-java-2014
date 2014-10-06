package ru.fizteh.fivt.students.RadimZulkaneev.shell;

import java.io.FileNotFoundException;
import java.util.Scanner;

public final class Interactive {
    private Interactive() {
    }
    public static void conv() {
        String currentDir = MyFunctions.myPwd();
        while (true) {
            System.out.print("$ ");
            Scanner in = new Scanner(System.in);
            String s;
            
            try {
                s = in.nextLine();
                s = s.trim();
                
                String[] current = s.split("\\s+");
                for (int i = 0; i < current.length; ++i) {
                    current[i].trim();
                }
                switch (current[0]) {
                case "cd":
                    try {
                        currentDir = MyFunctions.myCd(currentDir, 
                                current[1], false);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.out.println("cd: missing operand");
                    } catch (FileNotFoundException ex1) {
                        break;
                    }
                    break;
                case "ls":
                    MyFunctions.myLs(currentDir);
                    break;
                case "pwd":
                    System.out.println(currentDir);
                    break;
                case "cat":
                    try {
                        MyFunctions.myCat(currentDir, current[1]);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.out.println("cd: missing operand");
                    }
                    break;
                case "rm":
                    if (current[1].equals("-r")) {
                        if (current.length >= 3) {
                            MyFunctions.myRmDir(currentDir, current[2]);
                        } else {
                            System.out.println("No input file");
                        }
                    } else {
                        try {
                            MyFunctions.myRmFile(currentDir, current[1]);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            System.out.println("rm: No input");
                        }
                        
                    }
                    break;
                case "mkdir":
                    try {
                        MyFunctions.myMkdir(currentDir, current[1]);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.out.println("mkdir: No input");
                    }
                    break;
                case "":
                    break;
                case "cp":
                    try {
                        if (current[1].equals("-r")) {            
                            MyFunctions.myCpDir(currentDir, current[2],
                                    current[3], "cp");
                        } else {
                            MyFunctions.myCpFile(currentDir, current[1],
                                    current[2],
                                    "cp");
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.out.println("cp: incorrect input");
                    }
                    break;
                case "mv":
                    try {
                        MyFunctions.myMv(currentDir, current[1], current[2]);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.out.println("mv: incorrect input");
                    }
                    break;
                case "exit":
                    in.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println(current[0] + ": No such command");
                }
            } catch (Exception e) {
                System.exit(1);    
            }
            
        }
        
    }
}
