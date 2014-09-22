package ru.fizteh.fivt.students.kolmakov_sergey.shell;

import java.util.Scanner;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.NoSuchElementException;

public class ShellMain {

    private static boolean packageMode;
    private static String currentDirectory;

    private static boolean checkArguments(int from, int value, int to) {
        return from <= value && value <= to;
    }

    private static void pwd(String[] args)
            throws ShellException {
        if (!checkArguments(1, args.length, 1)) {
            ErrorHolder.errorCountArguments("pwd");
        }
        System.out.println(currentDirectory);
    }

    private static void ls(String[] args)
            throws ShellException {
        if (!checkArguments(1, args.length, 1)) {
            ErrorHolder.errorCountArguments("ls");
        }
        File directoryHolder = new File(currentDirectory);
        String[] fileNamesList = directoryHolder.list();
        for (String s : fileNamesList) {
            System.out.println(s);
        }
    }

    private static void cd(String[] args)
            throws ShellException {
        if (!checkArguments(2, args.length, 2)) {
            ErrorHolder.errorCountArguments("cd");
        }
        File currentFile;
        if (args[1].charAt(0) == '/') {
            currentFile = new File(args[1]);
        } else {
            currentFile = new File(currentDirectory
                    + File.separator + args[1]);
        }
        try {
            if (currentFile.exists() && currentFile.isDirectory()) {
                System.setProperty("user.dir", currentFile.getCanonicalPath());
                currentDirectory = System.getProperty("user.dir");
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            ErrorHolder.errorNoFile("cd", args[1]);
        }
    }

    private static void cat(String[] args)
            throws ShellException {
        if (!checkArguments(2, args.length, 2)) {
            ErrorHolder.errorCountArguments("cat");
        }
        File currentFile = new File(currentDirectory
                + File.separator + args[1]);
        if (currentFile.exists() && currentFile.isDirectory()) {
            ErrorHolder.errorIsDirectory("cat", args[1]);
        }
        try {
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(currentFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                reader.close();
            }
        } catch (IOException e) {
            ErrorHolder.errorNoFile("cat", args[1]);
        }
    }

    private static void mkdir(String[] args)
            throws ShellException {
        if (!checkArguments(2, args.length, 2)) {
            ErrorHolder.errorCountArguments("mkdir");
        }
        File directoryHolder = new File(currentDirectory
                + File.separator + args[1]);
        if (directoryHolder.exists()) {
            ErrorHolder.errorFileExists("mkdir", args[1]);
        } else if (!directoryHolder.mkdir()) {
            ErrorHolder.errorCreatingFile("mkdir");
        }
    }

    private static void delete(File file)
            throws ShellException {
        if (file.isDirectory()) {
            File[] fileNamesList = file.listFiles();
            if (fileNamesList == null) {
                ErrorHolder.errorCannotPerform("rm");
                return;
            }
            for (File f: fileNamesList) {
                delete(f);
            }
        }
        if (!file.delete()) {
            ErrorHolder.errorCannotPerform("rm");
        }
    }

    private static void rm(String[] args)
            throws ShellException {
        if (!checkArguments(2, args.length, 3)) {
            ErrorHolder.errorCountArguments("rm");
        }
        boolean recursive = false;
        if (args.length == 3) {
            if (!args[1].equals("-r")) {
                ErrorHolder.errorInvalidArgument("rm", args[1]);
            }
            recursive = true;
        }
        File currentFile = new File(
                currentDirectory + File.separator + args[args.length - 1]);
        if (!currentFile.exists()) {
            ErrorHolder.errorNoFile("rm", args[args.length - 1]);
        }
        if (currentFile.isDirectory() && !recursive) {
            ErrorHolder.errorIsDirectory("rm", args[args.length - 1]);
        }
        delete(currentFile);
    }

    private static void mv(String[] args)
            throws ShellException {
        if (!checkArguments(3, args.length, 3)) {
            ErrorHolder.errorCountArguments("mv");
        }
        File from = new File(currentDirectory
                + File.separator + args[args.length - 2]);
        File to = new File(currentDirectory
                + File.separator + args[args.length - 1]);
        if (!from.exists()) {
            ErrorHolder.errorNoFile("mv", from.getName());
        }
        if (from.equals(to)) {
            return;
        }
        try {
            copy(from, to);
        } catch (ShellException e) {
            String msg = e.getMessage();
            msg = msg.replace("cp", "mv");
            throw new ShellException(msg);
        }
        try {
            delete(from);
        } catch (ShellException e) {
            String msg = e.getMessage();
            msg = msg.replace("rm", "mv");
            throw new ShellException(msg);
        }
    }

    private static void copy(File from, File to)
            throws ShellException {
        File destination = null;
        try {
            if (to.exists()) {
                if (!to.isDirectory()) {
                    ErrorHolder.errorFileExists("cp", to.getName());
                    return;
                } else {
                    destination = new File(to.getCanonicalPath()
                            + File.separator + from.getName());
                }
            } else {
                destination = to;
            }
            Files.copy(from.toPath(), destination.toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            ErrorHolder.errorCannotPerform("cp");
        }
        if (from.isDirectory()) {
            File[] fileNamesList = from.listFiles();
            if (fileNamesList == null) {
                ErrorHolder.errorCannotPerform("cp");
                return;
            }
            for (File f: fileNamesList) {
                copy(f, destination);
            }
        }
    }

    private static void cp(String[] args)
            throws ShellException {
        if (!checkArguments(3, args.length, 4)) {
            ErrorHolder.errorCountArguments("cp");
        }
        boolean recursive = false;
        if (args.length == 4) {
            if (!args[1].equals("-r")) {
                ErrorHolder.errorInvalidArgument("rm", args[1]);
            }
            recursive = true;
        }
        File from = new File(currentDirectory
                + File.separator + args[args.length - 2]);
        File to = new File(currentDirectory
                + File.separator + args[args.length - 1]);
        if (!from.exists()) {
            ErrorHolder.errorNoFile("cp", from.getName());
        }
        if (from.isDirectory() && !recursive) {
            ErrorHolder.errorIsDirectory("cp", from.getName());
        }
        copy(from, to);
    }

    private static void exit(String[] args)
            throws ShellException, ShellExitException {
        if (!checkArguments(1, args.length, 1)) {
            ErrorHolder.errorCountArguments("exit");
        }
        throw new ShellExitException();
    }

    private static void execute(String command) throws ShellExitException {
        command = command.trim();
        String[] splitted = command.split("\\s+");
        try {
            switch (splitted[0]) {
                case "pwd":
                    pwd(splitted);
                    break;
                case "cd":
                    cd(splitted);
                    break;
                case "ls":
                    ls(splitted);
                    break;
                case "cat":
                    cat(splitted);
                    break;
                case "mkdir":
                    mkdir(splitted);
                    break;
                case "rm":
                    rm(splitted);
                    break;
                case "cp":
                    cp(splitted);
                    break;
                case "mv":
                    mv(splitted);
                    break;
                case "exit":
                    exit(splitted);
                    break;
                case "":
                    break;
                default:
                    ErrorHolder.errorUnknownCommand(splitted[0]);
                    break;
            }
        } catch (ShellException e) {
            System.err.println(e.getMessage());
            if (packageMode) {
                System.exit(-1);
            }
        }
    }

    public static void execCommands(String[] commands) {
        try {
            for (String command : commands) {
                execute(command);
            }
        } catch (ShellExitException e) {
            System.exit(0);
        }
    }

    private static void execLine(String line) {
        String[] commands = line.trim().split(";");
        execCommands(commands);
    }

    private static void packageAppender(String[] args) {
        StringBuilder commands = new StringBuilder();
        for (String arg: args) {
            commands.append(arg);
            commands.append(' ');
        }
        execLine(commands.toString());
    }

    private static void Interactive() {
        try (Scanner scan = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                String commands = scan.nextLine();
                execLine(commands);
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                packageMode = true;
            }
            currentDirectory = System.getProperty("user.dir");
            if (packageMode) {
                packageAppender(args);
            } else {
                Interactive();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
}

class ShellException extends Exception {
    public ShellException(String message) {
        super(message);
    }
}
class ShellExitException extends Exception {
}