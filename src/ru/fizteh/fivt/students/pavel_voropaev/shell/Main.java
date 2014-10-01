package ru.fizteh.fivt.students.pavel_voropaev.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class Main {
    public static String currentDir;

    public static void main(String[] args) {
        currentDir = System.getProperty("user.dir");
        if (args.length > 0) {
            try {
                packageMode(args);
            } catch (Exception e) {
                System.err.print(e.getMessage());
                System.exit(-1);
            }
        } else {
            try {
                interactiveMode();
            } catch (Exception e) {
                System.err.print(e.getMessage());
                System.exit(-1);
            }
        }
    }

    private static void packageMode(String[] args) {
        StringBuilder commandsLine = new StringBuilder();
        for (String arg : args) {
            if (!arg.equals(" ")) {
                commandsLine.append(arg);
                commandsLine.append(' ');
            }
        }

        String[] command = commandsLine.toString().split(";");
        for (String comm : command) {
            execCommand(comm.trim());
        }

    }

    private static void interactiveMode() {
        boolean exit = false;
        Scanner scan = new Scanner(System.in);
        System.out.print("$ ");
        String nextLine = scan.nextLine();
        while (!exit) {
            try {
                exit = execCommand(nextLine.trim());
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            } catch (IllegalStateException e) {
                System.err.println(e.getMessage());
            } finally {
                if (!exit) {
                    System.out.print("$ ");
                    nextLine = scan.nextLine();
                }

            }
        }
        scan.close();
    }

    private static boolean execCommand(String p)
            throws IllegalArgumentException {
        String[] command = splitCommand(p);
        if (command[0].equals("cd")) {
            if (command.length != 2) {
                throw new IllegalArgumentException("Incorrect usage of cd");
            }
            execCd(command[1]);
            return false;
        }
        if (command[0].equals("mkdir")) {
            if (command.length != 2) {
                throw new IllegalArgumentException("Incorrect usage of mkdir");
            }
            execMkdir(command[1]);
            return false;
        }
        if (command[0].equals("pwd")) {
            System.out.println(currentDir);
            return false;
        }
        if (command[0].equals("rm")) {
            if (command.length == 2) {
                execRm(command[1], false);
                return false;
            }
            if (command.length == 3 && command[1].equals("-r")) {
                execRm(command[2], true);
                return false;
            }
            throw new IllegalArgumentException("Incorrect usage of rm");
        }
        if (command[0].equals("cp")) {
            if (command.length == 3) {
                execCp(command[1], command[2], false);
                return false;
            }
            if (command.length == 4 && command[1].equals("-r")) {
                execCp(command[2], command[3], true);
                return false;
            }
            throw new IllegalArgumentException("Incorrect usage of cp");
        }
        if (command[0].equals("mv")) {
            if (command.length == 3) {
                execMv(command[1], command[2], false);
                return false;
            }
            if (command.length == 4 && command[1].equals("-r")) {
                execMv(command[2], command[3], true);
                return false;
            }
            throw new IllegalArgumentException("Incorrect usage of mv");
        }
        if (command[0].equals("ls")) {
            File currentFile = new File(currentDir);
            String[] fileList = currentFile.list();
            for (String file : fileList) {
                System.out.println(file);
            }
            return false;
        }
        if (command[0].equals("exit")) {
            return true;
        }

        if (command[0].equals("cat")) {
            if (command.length != 2) {
                throw new IllegalArgumentException("Incorrect usage of cat");
            }
            execCat(command[1]);
            return false;
        }
        throw new IllegalArgumentException("Unknown command: " + p);

    }

    private static String[] splitCommand(String p) {
        String[] command = p.split("\\s+");
        return command;
    }

    private static void execCd(String p) throws IllegalArgumentException {
        File currentFile;
        if (p.charAt(0) == '/') {
            currentFile = new File(p);
        } else {
            currentFile = new File(currentDir + File.separator + p);
        }

        if (!currentFile.exists() || !currentFile.isDirectory()) {
            throw new IllegalArgumentException("cd: '" + p
                    + "': No such file or directory");
        }

        try {
            System.setProperty("user.dir", currentFile.getCanonicalPath());
            currentDir = System.getProperty("user.dir");

        } catch (IOException e) {
            throw new IllegalArgumentException("cd: '" + p
                    + "': No such file or directory");
        }
    }

    private static void execMkdir(String p) throws IllegalArgumentException,
            IllegalStateException {
        File currentFile = new File(currentDir + File.separator + p);
        if (currentFile.exists()) {
            throw new IllegalArgumentException("mkdir: '" + p
                    + "': already exists");
        } else if (!currentFile.mkdir()) {
            throw new IllegalStateException("mkdir: '" + p
                    + "': Cannot create file or directory");
        }

    }

    private static void execRm(String p, boolean isRecursive)
            throws IllegalArgumentException, IllegalStateException {
        File currentFile = new File(currentDir + File.separator + p);
        if (!currentFile.exists()) {
            throw new IllegalArgumentException("rm: cannot remove '" + p
                    + "': No such file or directory");
        }
        if (currentFile.isDirectory() && !isRecursive) {
            throw new IllegalArgumentException("rm: " + p + ": is a directory");
        }

        try {
            rm(currentFile);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("rm: '" + p
                    + "': Cannot delete this file or directory");
        }
    }

    private static void rm(File currentFile) throws IllegalStateException {
        if (currentFile.isDirectory()) {
            File[] listFiles = currentFile.listFiles();
            if (listFiles == null) {
                throw new IllegalStateException();
            }
            for (File file : listFiles) {
                rm(file);
            }
        }
        if (!currentFile.delete()) {
            throw new IllegalStateException();
        }

    }

    private static void execCp(String src, String dest, boolean isRecursive)
            throws IllegalArgumentException, IllegalStateException {
        File srcFile = new File(currentDir + File.separator + src);
        File destFile = new File(currentDir + File.separator + dest);
        if (!srcFile.exists()) {
            throw new IllegalArgumentException("cp: '" + src
                    + "': No such file or directory");
        }

        if (srcFile.isDirectory() && !isRecursive) {
            throw new IllegalArgumentException("cp: " + src
                    + ": is a directory (not copied).");
        }

        cp(srcFile, destFile);

    }

    private static void cp(File srcFile, File destFile)
            throws IllegalArgumentException, IllegalStateException {
        File file = null;
        try {
            if (destFile.exists()) {
                if (!destFile.isDirectory()) {
                    throw new IllegalArgumentException("cp: '"
                            + destFile.getName() + "': File already exists");
                } else {
                    file = new File(destFile.getCanonicalPath()
                            + File.separator + srcFile.getName());
                }
            } else {
                file = destFile;
            }
            Files.copy(srcFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("cp: '" + file.getName()
                    + "': Cannot copy file");
        }
        if (srcFile.isDirectory()) {
            File[] listFiles = srcFile.listFiles();
            if (listFiles == null) {
                throw new IllegalStateException("cp: '" + srcFile.getName()
                        + "': Cannot copy file");
            }
            for (File f : listFiles) {
                cp(f, file);
            }
        }

    }

    private static void execMv(String src, String dest, boolean isRecursive)
            throws IllegalArgumentException, IllegalStateException {
        File srcFile = new File(currentDir + File.separator + src);
        File destFile = new File(currentDir + File.separator + dest);
        if (!srcFile.exists()) {
            throw new IllegalArgumentException("mv: '" + src
                    + "': No such file or directory");
        }

        if (srcFile.isDirectory() && !isRecursive) {
            throw new IllegalArgumentException("mv: " + src
                    + ": is a directory (not moved).");
        }

        try {
            cp(srcFile, destFile);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("mv: '" + srcFile.getName()
                    + "': Cannot copy file");
        }
        try {
            rm(srcFile);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("mv: '" + srcFile.getName()
                    + "': Cannot remove file");
        }

    }

    private static void execCat(String p) {

        File currentFile = new File(currentDir + File.separator + p);
        if (!currentFile.exists()) {
            throw new IllegalArgumentException("cat: '" + p
                    + "': No such file or directory");
        }
        if (currentFile.isDirectory()) {
            throw new IllegalArgumentException("cat: " + p + ": is a directory");
        }

        try {
            Scanner scanner = new Scanner(currentFile);
            while (scanner.hasNext()) {
                System.out.println(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException ioExcept) {
            throw new IllegalArgumentException("cat: '" + p
                    + "': Cannot read file");
        }

    }

}
