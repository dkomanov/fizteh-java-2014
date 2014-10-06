package ru.fizteh.fivt.students.egor_belikov.shell;
 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;
 
public class Shell {
    private static String path;
    private static boolean ispack;
    private static String sep = File.separator;
 
    public static void main(String[] args) {
        try {
            path = System.getProperty("user.home");
            ispack = (args.length == 0);
            if (args.length == 0) {
                pack(args);
            } else {
                interactive();
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            System.exit(1);
        }
    }
 
    private static void pack(String[] arg) throws Exception {
        StringBuilder input = new StringBuilder();
        for (String s: arg) {
            input.append(s);
            input.append(' ');
        }
        String[] comm = input.toString().trim().split(";");
        for (String s: comm) {
            run(s);
        }
    }
 
    private static void interactive() throws Exception {
        Scanner scan = new Scanner(System.in);
        try {
            String line = scan.nextLine();
            System.out.print("$ ");
            String[] comm = line.toString().trim().split(";");
            for (String s: comm) {
                run(s);
            }
        } catch (NoSuchElementException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }
        scan.close();
    }
    private static void run(String line) throws Exception {
        String[] args = line.trim().split("\\s+");
        try {
            switch (args[0]){
                case "cd":
                    if (args.length != 2) {
                        throw new Exception(
                                "cd: Invalid number of arguments");
                    } else {
                        cd(args);
                    }
                    break;
                case "mkdir":
                    if (args.length != 2) {
                        throw new Exception(
                                "mkdir: Invalid number of arguments");
                    } else {
                        mkdir(args);
                    }
                    break;
                case "pwd":
                    if (args.length != 1) {
                        throw new Exception(
                                "mkdir: Invalid number of arguments");
                    } else {
                        pwd();
                    }
                    break;
                case "rm":
                    if ((args.length != 2) && (args.length != 3)) {
                        throw new Exception(
                                "rm: Invalid number of arguments");
                    } else {
                        rm(args);
                    }
                    break;
                case "cp":
                    if ((args.length != 3) && (args.length != 4)) {
                        throw new Exception(
                                "cp: Invalid number of arguments");
                    } else {
                        cp(args);
                    }
                    break;
                case "mv":
                    if ((args.length != 3)) {
                        throw new Exception(
                                "mv: Invalid number of arguments");
                    } else {
                        mv(args);
                    }
                    break;
                case "ls":
                    if ((args.length != 1)) {
                        throw new Exception(
                                "ls: Invalid number of arguments");
                    } else {
                        ls();
                    }
                    break;
                case "exit":
                    System.exit(0);
                    break;
                case "cat":
                    if ((args.length != 2)) {
                        throw new Exception(
                                "cat: Invalid number of arguments");
                    } else {
                        cat(args);
                    }
                    break;
                case "":
                    break;
                default:
                    throw new Exception(
                            args[0] + ": Unknown command");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (ispack) {
                System.exit(-1);
            }
        }
    }
 
    private static void cat(String[] args) throws Exception {
        File file = new File(path + sep + args[1]);
        if (file.exists() && file.isDirectory()) {
            throw new Exception("cat: " + args[1] + ": is a directory");
        }
        try (BufferedReader bufferedReader = new BufferedReader(new
                FileReader(file));) {
            String line;
            line = bufferedReader.readLine();
            while (line != null) {
                System.out.println(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            throw new Exception(
                    "cat: " + args[1] + ": No such file or directory");
        }
    }
 
    private static void ls() {
        File file = new File(path);
        String[] output = file.list();
        for (String s : output) {
            System.out.println(s);
        }
    }
 
    private static void mv(String[] args) throws Exception {
        File from = new File(path
                + sep + args[args.length - 2]);
        File to = new File(path
                + sep + args[args.length - 1]);
        if (!from.exists()) {
            throw new Exception(
                    "mv: " + from.getName() + ": No such file or directory");
        }
        if (from.equals(to)) {
            return;
        }
        try {
            copyFile(from, to, "mv");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        try {
            delete(from, "mv");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
 
    private static void cp(String[] args) throws Exception {
        boolean r = false;
        if (args.length == 4) {
            if (!args[1].equals("-r")) {
                throw new Exception(
                        "cp: Invalid argument");
            }
            r = true;
        }
        File from = new File(path + sep + args[args.length - 2]);
        File to = new File(path + sep + args[args.length - 1]);
        if (!from.exists()) {
            throw new Exception(
                    "cp: " + args[args.length - 2] + ": No such file or directory");
        }
        if (from.isDirectory() && !r) {
            throw new Exception(
                    "cp: " + from.getName() + ": is a directory");
        }
        copyFile(from, to, "cp");
    }
 
    private static void rm(String[] args) throws Exception {
        boolean r = false;
        File file = new File(
                path + sep + args[args.length - 1]);
        if (args.length == 3) {
            r = true;
            if (!args[1].equals("-r")) {
                throw new Exception(
                        "rm: Invalid argument");
            }
        }
        if (!file.exists()) {
            throw new Exception(
                    "rm: cannot delete" + args[args.length - 1] + ": No such file or directory");
        }
        if (file.isDirectory() && !r) {
            throw new Exception(
                    "rm: Unable to create file or directory");
        }
        delete(file, "rm");
    }
 
    private static void pwd() {
        System.out.println(path);
    }
 
    private static void mkdir(String[] args) throws Exception {
        File newDirectory = new File(path + '/' + args[1]);
        if (newDirectory.exists()) {
            throw new Exception("mkdir: " + args[1] + ": File already exists");
        } else if (!newDirectory.mkdir()) {
            throw new Exception(
                    "mkdir: Unable to create file or directory");
        }
    }
 
    private static void cd(String[] args) throws Exception {
        File file;
        if (args[1].charAt(0) == '/') {
            file = new File(args[1]);
        } else {
            file = new File(path + sep + args[1]);
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                System.setProperty("user.dir", file.getCanonicalPath());
                path = System.getProperty("user.dir");
            } else {
                throw new Exception("cd: '" + args[1] + "': No such file or directory");
            }
        } else {
            throw new Exception(
                    "cd: '" + args[1] + "': No such file or directory");
        }
    }
    private static void delete(File f, String parentCommand)
            throws Exception {
        if (f.isDirectory()) {
            File[] fList = f.listFiles();
            if (fList == null) {
                throw new Exception(parentCommand + ": cannot delete");
            }
            for (File i: fList) {
                delete(i, parentCommand);
            }
        }
        if (!f.delete()) {
            throw new Exception(parentCommand + ": cannot delete");
        }
        return;
    }
    
    private static void copyFile(File from, File to, String parentCommand)
            throws Exception {
        File destination = null;
        try {
            if (to.exists()) {
                if (!to.isDirectory()) {
                    throw new Exception(
                            "cp: " + to.getName() + ": File already exists");
                } else {
                    destination = new File(to.getCanonicalPath()
                            + File.separator + from.getName());
                }
            } else {
                destination = to;
            }
            Files.copy(from.toPath(), destination.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            throw new Exception(
                    parentCommand + ": cannot perform this operation");
        }
        if (from.isDirectory()) {
            File[] fList = from.listFiles();
            if (fList == null) {
                throw new Exception(
                        parentCommand + ": cannot perform this operation");
            }
            for (File f: fList) {
                copyFile(f, destination, parentCommand);
            }
        }
    }
}
