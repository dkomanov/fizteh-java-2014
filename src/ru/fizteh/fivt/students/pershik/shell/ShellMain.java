package ru.fizteh.fivt.students.pershik.shell;

/**
 * @author pershik
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ShellMain {

    private static String workingDirectory;
    private static boolean batchMode;
    private static boolean exited;

    private static boolean checkArguments(int min, int max, int real) {
        return min <= real && real <= max;
    }

    private static void errorCntArguments(String command)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": invalid number of arguments");
    }

    private static void errorNoFile(String command, String file)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": " + file + ": No such file or directory");
    }

    private static void errorFileAlreadyExists(String command, String file)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": " + file + ": File already exists");
    }

    private static void errorCreatingFile(String command)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": cannot create file or directory");
    }

    private static void errorIsDirectory(String command, String file)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": " + file + ": is a directory");
    }

    private static void errorInvalidArgument(String command, String arg)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": " + arg + ": Invalid argument");
    }

    private static void errorCannotPerform(String command)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": cannot perform this operation");
    }

    private static void errorUnknownCommand(String command)
            throws InvalidCommandException {
        throw new InvalidCommandException(
                command + ": unknown command");
    }

    private static void pwd(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(0, 0, args.length - 1)) {
            errorCntArguments("pwd");
        }
        System.out.println(workingDirectory);
    }

    private static void cd(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("cd");
        }
        File curFile;
        if (args[1].charAt(0) == '/') {
            curFile = new File(args[1]);
        } else {
            curFile = new File(workingDirectory
                    + File.separator + args[1]);
        }
        try {
            if (curFile.exists() && curFile.isDirectory()) {
                System.setProperty("user.dir", curFile.getCanonicalPath());
                workingDirectory = System.getProperty("user.dir");
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            errorNoFile("cd", args[1]);
        }
    }

    private static void ls(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(0, 0, args.length - 1)) {
            errorCntArguments("ls");
        }
        File curDir = new File(workingDirectory);
        String[] content = curDir.list();
        if (content != null) {
            for (String curFile : content) {
                System.out.println(curFile);
            }
        }
    }

    private static void cat(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("cat");
        }
        File curFile = new File(workingDirectory
                + File.separator + args[1]);
        if (curFile.exists() && curFile.isDirectory()) {
            errorIsDirectory("cat", args[1]);
        }
        try {
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(curFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            errorNoFile("cat", args[1]);
        }
    }

    private static void mkdir(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("mkdir");
        }
        File curDir = new File(workingDirectory
                + File.separator + args[1]);
        if (curDir.exists()) {
            errorFileAlreadyExists("mkdir", args[1]);
        } else if (!curDir.mkdir()) {
            errorCreatingFile("mkdir");
        }
    }

    private static void delete(File file)
            throws InvalidCommandException {
        if (file.isDirectory()) {
            File[] content = file.listFiles();
            if (content == null) {
                errorCannotPerform("rm");
                return;
            }
            for (File children: content) {
                delete(children);
            }
        }
        if (!file.delete()) {
            errorCannotPerform("rm");
        }
    }

    private static void rm(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(1, 2, args.length - 1)) {
            errorCntArguments("rm");
        }
        boolean recursive = false;
        if (args.length == 3) {
            if (!args[1].equals("-r")) {
                errorInvalidArgument("rm", args[1]);
            }
            recursive = true;
        }
        File curFile = new File(
                workingDirectory + File.separator + args[args.length - 1]);
        if (!curFile.exists()) {
            errorNoFile("rm", args[args.length - 1]);
        }
        if (curFile.isDirectory() && !recursive) {
            errorIsDirectory("rm", args[args.length - 1]);
        }
        delete(curFile);
    }

    private static void copy(File fromFile, File toFile)
            throws InvalidCommandException {
        File destFile = null;
        try {
            if (toFile.exists()) {
                if (!toFile.isDirectory()) {
                    errorFileAlreadyExists("cp", toFile.getName());
                    return;
                } else {
                    destFile = new File(toFile.getCanonicalPath()
                            + File.separator + fromFile.getName());
                }
            } else {
                destFile = toFile;
            }
            Files.copy(fromFile.toPath(), destFile.toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            errorCannotPerform("cp");
        }
        if (fromFile.isDirectory()) {
            File[] content = fromFile.listFiles();
            if (content == null) {
                errorCannotPerform("cp");
                return;
            }
            for (File children: content) {
                copy(children, destFile);
            }
        }
    }

    private static void cp(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(2, 3, args.length - 1)) {
            errorCntArguments("cp");
        }
        boolean recursive = false;
        if (args.length == 4) {
            if (!args[1].equals("-r")) {
                errorInvalidArgument("rm", args[1]);
            }
            recursive = true;
        }
        File fromFile = new File(workingDirectory
                + File.separator + args[args.length - 2]);
        File toFile = new File(workingDirectory
                + File.separator + args[args.length - 1]);
        if (!fromFile.exists()) {
            errorNoFile("cp", fromFile.getName());
        }
        if (fromFile.isDirectory() && !recursive) {
            errorIsDirectory("cp", fromFile.getName());
        }
        copy(fromFile, toFile);
    }

    private static void mv(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(2, 2, args.length - 1)) {
            errorCntArguments("mv");
        }
        File fromFile = new File(workingDirectory
                + File.separator + args[args.length - 2]);
        File toFile = new File(workingDirectory
                + File.separator + args[args.length - 1]);
        if (!fromFile.exists()) {
            errorNoFile("mv", fromFile.getName());
        }
        if (fromFile.equals(toFile)) {
            return;
        }
        try {
            copy(fromFile, toFile);
            delete(fromFile);
        } catch (InvalidCommandException e) {
            String msg = e.getMessage();
            msg = msg.replace("cp", "mv");
            msg = msg.replace("rm", "mv");
            throw new InvalidCommandException(msg);
        }
    }

    private static void exit(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(0, 0, args.length - 1)) {
            errorCntArguments("exit");
        }
        exited = true;
    }

    private static void execute(String command) {
        command = command.trim();
        String[] tokens = command.split("\\s+");
        try {
            switch (tokens[0]) {
                case "pwd":
                    pwd(tokens);
                    break;
                case "cd":
                    cd(tokens);
                    break;
                case "ls":
                    ls(tokens);
                    break;
                case "cat":
                    cat(tokens);
                    break;
                case "mkdir":
                    mkdir(tokens);
                    break;
                case "rm":
                    rm(tokens);
                    break;
                case "cp":
                    cp(tokens);
                    break;
                case "mv":
                    mv(tokens);
                    break;
                case "exit":
                    exit(tokens);
                    break;
                case "":
                    break;
                default:
                    errorUnknownCommand(tokens[0]);
                    break;
            }
        } catch (InvalidCommandException e) {
            System.err.println(e.getMessage());
            if (batchMode) {
                System.exit(-1);
            }
        }
    }

    public static void runCommands(String[] commands) {
        for (String command: commands) {
            execute(command);
            if (exited) {
                break;
            }
        }
    }

    private static void execLine(String line) {
        String[] commands = line.trim().split(";");
        runCommands(commands);
    }

    private static void runBatch(String[] args) {
        StringBuilder allCommands = new StringBuilder();
        for (String arg: args) {
            allCommands.append(arg);
            allCommands.append(" ");
        }
        execLine(allCommands.toString());
    }

    private static void runInteractive() {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                if (!sc.hasNextLine()) {
                    break;
                }
                String allCommands = sc.nextLine();
                execLine(allCommands);
                if (exited) {
                    break;
                }
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void init(String[] args) {
        if (args.length > 0) {
            batchMode = true;
        }
        workingDirectory = System.getProperty("user.dir");
    }

    public static void main(String[] args) {
        try {
            init(args);
            if (batchMode) {
                runBatch(args);
            } else {
                runInteractive();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}

class InvalidCommandException extends Exception {
    public InvalidCommandException(String message) {
        super(message);
    }
}
