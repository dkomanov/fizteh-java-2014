package ru.fizteh.fivt.students.Volodin_Denis.Shell;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author volodden
 */

public class Shell {
    Shell() {
    }
    static final int SUCCESS = 0;
    static final int ERROR = 1;
    
    public static void main(final String[] args) {
        if (args.length == 0) {     //interactive mode
            do {
                Scanner scanner = new Scanner(System.in);
                try {
                    System.out.print("$ ");
                    String[] shellIn = scanner.nextLine().split(";");
                    for (int i = 0; i < shellIn.length; ++i) {
                        if (shellIn[i].length() > 0) {
                            String[] buffer = shellIn[i].trim().split("\\s+");
                            try {
                                shellParser(buffer);
                            } catch (Exception except) {
                                System.err.println("$ " + except.getMessage());
                            }
                        }
                    }
                } catch (Exception except) {
                    System.err.println("$ " + except.getMessage());
                }
            } while (true);
        } else {
            try {
                StringBuilder helpArray = new StringBuilder();
                
                for (int i = 0; i < args.length; ++i) {
                    helpArray.append(args[i]).append(' ');
                }
                
                String longStr = helpArray.toString();
                String[] shellIn = longStr.split(";");
                for (int i = 0; i < shellIn.length; ++i) {
                    if (shellIn[i].length() > 0) {
                        String[] buffer = shellIn[i].trim().split("\\s+");
                        try {
                            shellParser(buffer);
                        } catch (Exception except) {
                            System.err.println("$ " + except.getMessage());
                        }
                    }
                }
            } catch (Exception except) {
                System.err.println("$ " + except.getMessage());
            }
        }
    }

    //
    // basic functions
    //
    // begin
    //
    
    private static void shellCd(final String[] args) throws Exception {
        if (args.length != 2) {
            shellWrongQuantity("cd");
        }
        if (args[1].isEmpty()) {
            shellNoName("cd");
        }
        
        try {
            Path pathToFile = Paths.get(args[1]).normalize();
            if (!pathToFile.isAbsolute()) {
                pathToFile = Paths.get(System.getProperty("user.dir"),
                             pathToFile.toString()).normalize();
            }
            if (pathToFile.toFile().exists()) {
                if (pathToFile.toFile().isDirectory()) {
                    System.setProperty("user.dir", pathToFile.toString());
                } else {
                    shellNotDirectory("cd", args[1]);
                }
            } else {
                shellNotExist("cd", args[1]);
            }
        } catch (InvalidPathException invExcept) {
            shellInvalidName("cd", args[1]);
        } catch (SecurityException secExcept) {
            shellSecurity("cd", args[1]);
        }
    }
    
    private static void shellMkdir(final String[] args) throws Exception {
        if (args.length != 2) {
            shellWrongQuantity("mkdir");
        }
        if (args[1].isEmpty()) {
            shellNoName("mkdir");
        }
        
        try {
            Path pathToFile = Paths.get(args[1]).normalize();
            if (!pathToFile.isAbsolute()) {
                pathToFile = Paths.get(System.getProperty("user.dir"),
                             pathToFile.toString()).normalize();
            }
            
            if (pathToFile.toFile().exists()) {
                shellAlreadyExist("mkdir", args[1]);
            } else {
                if (!pathToFile.toFile().mkdir()) {
                    shellNotMkdir("mkdir", args[1]);
                }
            }
        } catch (InvalidPathException invExcept) {
            shellInvalidName("mkdir", args[1]);
        } catch (SecurityException secExcept) {
            shellSecurity("mkdir", args[1]);
        }
    }
    
    private static void shellPwd(final String[] args) throws Exception {
        if (args.length != 1) {
            shellWrongQuantity("pwd");
        }
        
        try {
            Path pathToFile = Paths.get(
                System.getProperty("user.dir")).normalize();
                System.out.println(pathToFile.toString());
        } catch (SecurityException secExcept) {
            shellSecurity("pwd", "current directory");
        }
    }
    
    private static void shellRm(final String[] args) throws Exception {
        if ((args.length != 2) && (args.length != 3)) {
            shellWrongQuantity("rm");
        }
        if (args[args.length - 1].isEmpty()) {
            shellNoName("rm");
        }
        if (args.length == 3) {
            if (!args[1].equals("-r")) {
                shellWrongKey("rm", "-r");
            }
            try {
                Path pathToFile = Paths.get(args[2]).normalize();
                if (!pathToFile.isAbsolute()) {
                    pathToFile = Paths.get(System.getProperty("user.dir"),
                                           args[2]).normalize();
                }
                if (!pathToFile.toFile().exists()) {
                    shellNotExist("rm", args[2]);
                }
                if (!pathToFile.toFile().isDirectory()) {
                    shellNotDirectory("rm", args[2]);
                }
                
                String[] names = pathToFile.toFile().list();
                
                if (names.length != 0) {
                    for (int i = 0; i < names.length; ++i) {
                        if (Paths.get(pathToFile.toString(),
                              names[i]).normalize().toFile().isDirectory()) {
                            System.setProperty("user.dir",
                                               pathToFile.toString());
                            String[] helpArray = new String[]
                                     {"rm", "-r", names[i]};
                            shellRm(helpArray);
                            System.setProperty("user.dir",
                                               pathToFile.getParent().toString());
                        }
                        if (Paths.get(pathToFile.toString(),
                                      names[i]).normalize().toFile().isFile()) {
                            String[] helpArray = new String[]
                                     {"rm", Paths.get(pathToFile.toString(),
                                      names[i]).normalize().toString() };
                            shellRm(helpArray);
                        }
                    }
                }
                if (!pathToFile.toFile().delete()) {
                    shellSmthWrong("rm");
                }
            
            } catch (InvalidPathException invExcept) {
                shellInvalidName("rm", args[2]);
            } catch (SecurityException secExcept) {
                shellSecurity("rm", args[2]);
            }
        } else {
            try {
                Path pathToFile = Paths.get(args[1]).normalize();
                
                if (!pathToFile.isAbsolute()) {
                    pathToFile = Paths.get(System.getProperty("user.dir"),
                                           args[1]).normalize();
                }
                if (!pathToFile.toFile().exists()) {
                    shellNotExist("rm", args[1]);
                }
                if (!pathToFile.toFile().isFile()) {
                    shellNotFile("rm", args[1]);
                }
                if (!pathToFile.toFile().delete()) {
                    shellSmthWrong("rm");
                }
            } catch (InvalidPathException invExcept) {
                shellInvalidName("rm", args[1]);
            } catch (SecurityException secExcept) {
                shellSecurity("rm", args[1]);
            }
        }
    }
    
    private static void shellCp(final String[] args) throws Exception {
        if ((args.length != 3) && (args.length != 4)) {
            shellWrongQuantity("cp");
        }
        if ((args[args.length - 1].isEmpty())
         || (args[args.length - 2].isEmpty())) {
            shellNoName("cp");
        }
        
        if (args.length == 3) {
            try {
                Path pathToFile = Paths.get(args[1]).normalize();
                if (!pathToFile.isAbsolute()) {
                    pathToFile = Paths.get(
                                           System.getProperty("user.dir").toString(),
                                           pathToFile.toString()).normalize();
                }
                if (!pathToFile.toFile().exists()) {
                    shellNotExist("cp", args[1]);
                }
                if (!pathToFile.toFile().isFile()) {
                    shellNotFile("cp", args[1]);
                }
                Path pathToNewFile = Paths.get(args[2]).normalize();
                if (!pathToNewFile.isAbsolute()) {
                    pathToNewFile = Paths.get(
                                              System.getProperty("user.dir").toString(),
                                              pathToNewFile.toString()).normalize();
                }
                if (!pathToNewFile.getParent().toFile().exists()) {
                    shellNotExist("cp", pathToNewFile.getParent().toString());
                }
                if (pathToNewFile.toString().equals(pathToFile.toString())) {
                    shellEqualNames("cp");
                }
                if (pathToNewFile.toFile().isDirectory()) {
                    pathToNewFile = Paths.get(pathToNewFile.toString(),
                                              pathToFile.getFileName().toString()).normalize();
                }
                Files.copy(pathToFile, pathToNewFile,
                           StandardCopyOption.COPY_ATTRIBUTES,
                           StandardCopyOption.REPLACE_EXISTING);
            } catch (InvalidPathException invExcept) {
                shellInvalidName("cp", args[1]);
            } catch (SecurityException secExcept) {
                shellSecurity("cp", args[1]);
            } catch (IOException ioExcept) {
                shellWrongInput("cp");
            }
        } else {
            if (!args[1].equals("-r")) {
                shellWrongKey("cp", "-r");
            }
            try {
                Path pathToFile = Paths.get(args[2]).normalize();
                if (!pathToFile.isAbsolute()) {
                    pathToFile = Paths.get(System.getProperty("user.dir").toString(),
                                           pathToFile.toString()).normalize();
                }
                if (!pathToFile.toFile().exists()) {
                    shellNotExist("cp", args[2]);
                }
                if (!pathToFile.toFile().isDirectory()) {
                    shellNotDirectory("cp", args[2]);
                }
                Path pathToNewFile = Paths.get(args[3]).normalize();
                if (!pathToNewFile.isAbsolute()) {
                    pathToNewFile = Paths.get(System.getProperty("user.dir").toString(),
                                              pathToNewFile.toString()).normalize();
                }
                if (!pathToNewFile.toFile().isDirectory()) {
                    shellNotDirectory("cp", args[3]);
                }
                if (pathToNewFile.toString().equals(pathToFile.toString())) {
                    shellEqualNames("cp");
                }
                if (pathToNewFile.toString().startsWith(pathToFile.toString())) {
                    shellIntoSelf("cp");
                }
                
                pathToNewFile = Paths.get(pathToNewFile.toString(),
                                          pathToFile.getFileName().toString()).normalize();
                if (!pathToNewFile.toFile().exists()) {
                    if (!pathToNewFile.toFile().mkdir()) {
                        shellNotMkdir("mkdir", args[1]);
                    }
                }
                
                String[] names = pathToFile.toFile().list();
                
                if (names.length != 0) {
                    for (int i = 0; i < names.length; ++i) {
                        if (Paths.get(pathToFile.toString(),
                                      names[i]).normalize().toFile().isDirectory()) {
                            System.setProperty("user.dir",
                                               pathToFile.toString());
                            String[] helpArray = new String[]
                                     {"cp", "-r", names[i],
                                      pathToNewFile.toString() };
                            shellCp(helpArray);
                            System.setProperty("user.dir",
                                               pathToFile.getParent().toString());
                        }
                        if (Paths.get(pathToFile.toString(),
                                      names[i]).normalize().toFile().isFile()) {
                            String[] helpArray = new String[]
                                     {"cp", Paths.get(pathToFile.toString(),
                                      names[i]).normalize().toString(),
                                      pathToNewFile.toString()};
                            shellCp(helpArray);
                        }
                    }
                }
            } catch (InvalidPathException invExcept) {
                shellInvalidName("cp", args[2]);
            } catch (SecurityException secExcept) {
                shellSecurity("cp", args[2]);
            } catch (IOException ioExcept) {
                shellWrongInput("cp");
            }
        }
    }
    
    private static void shellMv(final String[] args) throws Exception {
        if (args.length != 3) {
            shellWrongQuantity("mv");
        }
        if ((args[args.length - 1].isEmpty())
         || (args[args.length - 2].isEmpty())) {
            shellNoName("mv");
        }
        
        try {
            Path pathToFile = Paths.get(args[1]).normalize();
            if (!pathToFile.isAbsolute()) {
                pathToFile = Paths.get(System.getProperty("user.dir").toString(),
                                       pathToFile.toString()).normalize();
            }
            if (!pathToFile.toFile().exists()) {
                shellNotExist("mv", args[1]);
            }
            if (pathToFile.toFile().isFile()) {
                Path pathToNewFile = Paths.get(args[2]).normalize();
                if (!pathToNewFile.isAbsolute()) {
                pathToNewFile = Paths.get(System.getProperty("user.dir").toString(),
                                          pathToNewFile.toString()).normalize();
                }
                if (!pathToNewFile.getParent().toFile().exists()) {
                    shellNotExist("mv", pathToNewFile.getParent().toString());
                }
                if (pathToNewFile.toString().equals(pathToFile.toString())) {
                    shellEqualNames("mv");
                }
                if (pathToNewFile.toFile().isDirectory()) {
                    pathToNewFile = Paths.get(pathToNewFile.toString(),
                                              pathToFile.getFileName().toString()).normalize();
                }
                Files.move(pathToFile, pathToNewFile, StandardCopyOption.REPLACE_EXISTING);
            }
            if (pathToFile.toFile().isDirectory()) {
                Path pathToNewFile = Paths.get(args[2]).normalize();
                if (!pathToNewFile.isAbsolute()) {
                    pathToNewFile = Paths.get(System.getProperty("user.dir").toString(),
                                              pathToNewFile.toString()).normalize();
                }
                if (!pathToNewFile.getParent().toFile().exists()) {
                    shellNotExist("mv", pathToNewFile.getParent().toString());
                }
                if (pathToNewFile.toString().equals(pathToFile.toString())) {
                    shellEqualNames("mv");
                }
                if (pathToNewFile.toString().startsWith(pathToFile.toString())) {
                    shellIntoSelf("mv");
                }
                
                String[] helpArray1 = new String[]
                         {"cp", "-r", pathToFile.toString(),
                          pathToNewFile.toString()};
                shellCp(helpArray1);
                String[] helpArray2 = new String[]
                         {"rm", "-r", pathToFile.toString()};
                shellRm(helpArray2);
            }
        } catch (InvalidPathException invExcept) {
            shellInvalidName("mv", args[1]);
        } catch (SecurityException secExcept) {
            shellSecurity("mv", args[1]);
        } catch (IOException ioExcept) {
            shellWrongInput("mv");
        }
    }
    
    private static void shellLs(final String[] args) throws Exception {
        if (args.length != 1) {
            shellWrongQuantity("ls");
        }

        try {
            String[] names = new File(System.getProperty("user.dir")).list();
            if (names.length != 0) {
                for (int i = 0; i < names.length; ++i) {
                    System.out.println(names[i]);
                }
            }
        } catch (SecurityException secExcept) {
            shellAccessProhibited("ls", args[1]);
        }
    }
    
    private static void shellExit(final String[] args) throws Exception {
        System.exit(SUCCESS);
    }
    
    private static void shellCat(final String[] args) throws Exception {
        if (args.length != 2) {
           shellWrongQuantity("cat");
        }
        if (args[1].isEmpty()) {
            shellNoName("cat");
        }
        
        try {
            File fileForCat = Paths.get(System.getProperty("user.dir"),
                                        args[1]).normalize().toFile();
            System.out.println(fileForCat.toString());
            if (!fileForCat.exists()) {
                shellNotExist("cat", args[1]);
            }
            if (!fileForCat.isFile()) {
                shellNotFile("cat", args[1]);
            }
            try (Scanner scanner = new Scanner(fileForCat)) {
                while (scanner.hasNext()) {
                    System.out.println(scanner.nextLine());
                }
            } catch (IOException ioExcept) {
                shellWrongInput("cat");
            }
        } catch (InvalidPathException invExcept) {
            shellInvalidName("cat", args[1]);
        } catch (SecurityException secExcept) {
            shellSecurity("cat", args[1]);
        }
    }
    
    //
    // basic functions
    //
    // end
    //

    private static void shellParser(final String[] buffer) throws Exception {
        switch(buffer[0]) {
            case "cd":
                shellCd(buffer);
                break;
            case "mkdir":
                shellMkdir(buffer);
                break;
            case "pwd":
                shellPwd(buffer);
                break;
            case "rm":
                shellRm(buffer);
                break;
            case "cp":
                shellCp(buffer);
                break;
            case "mv":
                shellMv(buffer);
                break;
            case "ls":
                shellLs(buffer);
                break;
            case "exit":
                shellExit(buffer);
                break;
            case "cat":
                shellCat(buffer);
                break;
            default:
                System.err.println("Command does not exist: [" + buffer[0] + "]");
        }
    }
    
    private static void shellAccessProhibited(final String commandName,
                                              final String name) throws Exception {
        throw new Exception(commandName
                            + ":  access to list of [" + name + "] is phohibited.");
    }
    
    private static void shellAlreadyExist(final String commandName,
                                          final String name) throws Exception {
        throw new Exception(commandName
                            + ": the file [" + name + "] already exists.");
    }

    private static void shellEqualNames(final String commandName) throws Exception {
        throw new Exception(commandName
                            + ": files are same.");
    }
    
    private static void shellIntoSelf(final String commandName) throws Exception {
        throw new Exception(commandName
                            + ": copy into self.");
    }
    
    private static void shellInvalidName(final String commandName, 
                                         final String name) throws Exception {
        throw new Exception(commandName
                            + ": [" + name + "] is invalid name.");
    }

    private static void shellNotDirectory(final String commandName, 
                                          final String name) throws Exception {
        throw new Exception(commandName
                            + ": [" + name + "] is not a directory.");
    }
    
    private static void shellNotFile(final String commandName, 
                                     final String name) throws Exception {
        throw new Exception(commandName
                            + ": [" + name + "] is not a file.");
    }
    
    private static void shellNotExist(final String commandName,
                                      final String name) throws Exception {
        throw new Exception(commandName
                            + ": the file [" + name + "] is not exists.");
    }
    
    private static void shellNotMkdir(final String commandName,
                                      final String name) throws Exception {
        throw new Exception(commandName
                            + ": failed to create a directory [" + name + "].");
    }
    
    private static void shellNoName(final String commandName) throws Exception {
        throw new Exception(commandName
                            + ": no file name.");
    }
    
    private static void shellSecurity(final String commandName,
                                      final String name) throws Exception {
        throw new Exception(commandName
                            + ": access to the [" + name + "] is prohibeted.");
    }
    
    private static void shellSmthWrong(final String commandName) throws Exception {
        throw new Exception(commandName
                            + ": something went wrong.");
    }

    private static void shellWrongInput(final String commandName) throws Exception {
        throw new Exception(commandName
                            + ": wrong input.");
    }
    
    private static void shellWrongKey(final String commandName, 
                                      final String key) throws Exception {
        throw new Exception(commandName
                            + ": key is wrong. Use [" + key + "].");
    }
    
    private static void shellWrongQuantity(final String commandName) throws Exception {
        throw new Exception(commandName
                            + ": wrong quantity of arguments.");
    }
}
