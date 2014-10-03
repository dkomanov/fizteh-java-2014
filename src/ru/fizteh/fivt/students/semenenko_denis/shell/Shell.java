package ru.fizteh.fivt.students.semenenko_denis.shell;

/**
 * Created by denny_000 on 23.09.2014.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;
import java.util.Scanner;

private class InvalidCommandException extends Exception {
    public InvalidCommandException(String message) {
        super(message);
    }
}

public class Shell {

    private static String currentDirectory;
    private static boolean batchMode;
    private static boolean isExit;

    public static void main(String[] args) {
        if (args.length > 0) {
            batchMode = true;
        } else {
            batchMode = false;
        }
        currentDirectory = System.getProperty("user.dir");
        try {
            if (batchMode) {
                execBatchMode(args);
            } else {
                execInteractiveMode();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void execInteractiveMode() {
        try {
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                String commandsLine = sc.nextLine();
                String[] commandsArr = commandsLine.trim().split(";");
                for (String command : commandsArr) {
                    execCommand(command);
                    if (isExit) {
                        break;
                    }
                }
                if (isExit) {
                    break;
                }
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void execBatchMode(String[] args) {
        StringBuilder commands = new StringBuilder();
        for (String command : args) {
            commands.append(command + " ");
        }
        String commandsLine = commands.toString();
        String[] commandsArr = commandsLine.trim().split(";");
        for (String command : commandsArr) {
            execCommand(command);
            if (isExit) {
                break;
            }
        }
    }


    private static void execCommand(String command) {
        command = command.trim();
        String[] parts = command.split("\\s+");
        try {
            if (parts[0].equals("pwd")) {
                pwd(parts);

            } else if (parts[0].equals("cd")) {
                cd(parts);

            } else if (parts[0].equals("ls")) {
                ls(parts);

            } else if (parts[0].equals("cat")) {
                cat(parts);

            } else if (parts[0].equals("mkdir")) {
                mkdir(parts);

            } else if (parts[0].equals("rm")) {
                rm(parts);

            } else if (parts[0].equals("cp")) {
                cp(parts);

            } else if (parts[0].equals("mv")) {
                mv(parts);

            } else if (parts[0].equals("exit")) {
                exit(parts);

            } else if (parts[0].equals("")) {
                int i = 0;
            } else {
                errorUnknownCommand(parts[0]);

            }
        } catch (InvalidCommandException e) {
            System.err.println(e.getMessage());
            if (batchMode) {
                System.exit(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static boolean checkArguments(int min, int max, int real) {
        return min <= real && real <= max;
    }

    private static void pwd(String[] args) {
        System.out.println(currentDirectory);
    }

    private static void ls(String[] args) {
        File curDir = new File(currentDirectory);
        String[] filesList = curDir.list();
        if (filesList != null) {
            for (String file : filesList) {
                System.out.println(file);
            }
        }
    }

    private static void cd(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("cd");
        }
        Path path = new File(currentDirectory).toPath();

        File newPath = path.resolve(args[1]).toAbsolutePath().toFile();
        try {
            if (newPath.exists() && newPath.isDirectory()) {
                System.setProperty("user.dir", newPath.getCanonicalPath());
            } else if (!newPath.exists()) {
                throw new Exception("path '" + newPath.toString() + "' doesn't exist");
            } else {
                throw new Exception(args[1] + " is not a directory");
            }
            currentDirectory = System.getProperty("user.dir");
        } catch (Exception e) {
            errorNoFile("cd", args[1]);
        }
    }

    private static void cat(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("cat");
        }
        File curFile = new File(currentDirectory
                + File.separator + args[1]);
        if (curFile.exists() && curFile.isDirectory()) {
            errorIsDirectory("cat", args[1]);
        }
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(curFile));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();

        } catch (IOException e) {
            errorNoFile("cat", args[1]);
        }
    }

    private static void mkdir(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(1, 1, args.length - 1)) {
            errorCntArguments("mkdir");
        }
        File curDir = new File(currentDirectory
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
            for (File children : content) {
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
                currentDirectory + File.separator + args[args.length - 1]);
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
            for (File children : content) {
                copy(children, destFile);
            }
        }
    }

    private static void mv(String[] args)
            throws Exception {
        if (!checkArguments(2, 2, args.length - 1)) {
            errorCntArguments("mv");
        }
        File fromFile = new File(currentDirectory
                + File.separator + args[args.length - 2]);
        File toFile = new File(currentDirectory
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
        File fromFile = new File(currentDirectory
                + File.separator + args[args.length - 2]);
        File toFile = new File(currentDirectory
                + File.separator + args[args.length - 1]);
        if (!fromFile.exists()) {
            errorNoFile("cp", fromFile.getName());
        }
        if (fromFile.isDirectory() && !recursive) {
            errorIsDirectory("cp", fromFile.getName());
        }
        copy(fromFile, toFile);
    }

    private static void exit(String[] args)
            throws InvalidCommandException {
        if (!checkArguments(0, 0, args.length - 1)) {
            errorCntArguments("exit");
        }
        isExit = true;
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
}

