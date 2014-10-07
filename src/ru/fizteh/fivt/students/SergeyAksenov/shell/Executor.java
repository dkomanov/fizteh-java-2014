package ru.fizteh.fivt.students.SergeyAksenov.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;


public final class Executor {

    public static boolean checkArgNumber(int from, int value, int to) {
        return from <= value && value <= to;
    }

    public static void execute(final HashMap<String, Command> commandMap,
                               String[] commands, final Environment env)
            throws ShellExitException {
        try {
            if (commands[0].equals("")) {
                return;
            }
            Command cmd = commandMap.get(commands[0]);
            if (cmd == null) {
                ErrorHandler.unknownCommand(commands[0]);
            }
            commandMap.get(commands[0]).run(commands, env);
        } catch (ShellException e) {
            System.err.println(e.getMessage());
            if (env.packageMode) {
                System.exit(-1);
            }
        }
    }

    public static void execLine(String Line,
                                final HashMap<String, Command> commandMap,
                                final Environment env)
            throws ShellExitException {
        String[] commands = Line.trim().split(";");
        try {
            for (String command : commands) {
                command = command.trim();
                String[] splittedCommand = command.split("\\s+");
                execute(commandMap, splittedCommand, env);
            }
        } catch (ShellExitException e) {
            System.exit(0);
        }
    }

    public static void delete(File fileToRem)
            throws ShellException {
        if (fileToRem.isDirectory()) {
            File[] files = fileToRem.listFiles();
            if (files == null) {
                ErrorHandler.canNotPerform("rm");
            }
            for (File file : files) {
                delete(file);
            }
        }
        if (!fileToRem.delete()) {
            ErrorHandler.canNotPerform("rm");
        }

    }

    public static void copy(File src, File dst)
            throws ShellException {
        File finalDst = null;
        try {
            if (dst.exists()) {
                if (!dst.isDirectory()) {
                    ErrorHandler.fileExist("cp", dst.getName());
                    return;
                } else {
                    finalDst = new File(dst.getCanonicalPath()
                            + File.separator + src.getName());
                }
            } else {
                finalDst = dst;
            }
            Files.copy(src.toPath(), dst.toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException E) {
            ErrorHandler.canNotPerform("cp");
        }
        if (src.isDirectory()) {
            File[] files = src.listFiles();
            if (files == null) {
                ErrorHandler.canNotPerform("cp");
                return;
            }
            for (File f : files) {
                Executor.copy(f, finalDst);
            }
        }
    }

    public static void interactiveMode(
            final HashMap<String, Command> commandMap,
            final Environment env)
            throws ShellException, ShellExitException {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                if (!scanner.hasNextLine()) {
                    System.exit(0);
                }
                String command = scanner.nextLine();
                execLine(command, commandMap, env);
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void packageAppender(final String[] args,
                                       final HashMap<String, Command>
                                               commandMap,
                                       final Environment env)
            throws ShellExitException {
        StringBuilder commands = new StringBuilder();
        for (String arg : args) {
            commands.append(arg);
            commands.append(' ');
        }
        execLine(commands.toString(), commandMap, env);
    }
}
