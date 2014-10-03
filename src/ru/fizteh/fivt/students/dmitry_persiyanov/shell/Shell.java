package ru.fizteh.fivt.students.dmitry_persiyanov.shell;

import ru.fizteh.fivt.students.dmitry_persiyanov.shell.commands.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public final class Shell {
    private static String workingDir = System.getProperty("user.dir");

    public static String getWorkingDir() { return workingDir; }
    public static void setWorkingDir(final String newWorkingDir) { workingDir = new String(newWorkingDir); }

    public static void main(final String[] args) {
        if (args.length == 0) {
            interactiveShell();
        } else {
            packetShell(args);
        }
    }

    private static void interactiveShell() {
        System.out.print("$ ");
        try (Scanner in = new Scanner(System.in)) {
            while (true) {
                String cmd = in.nextLine().trim();
                int separatorIndex = cmd.indexOf(';');
                if (separatorIndex != -1) {
                    if (cmd.length() > separatorIndex + 1) {
                        String[] args = cmd.split(";");
                        packetShell(args);
                        System.out.print("$ ");
                        continue;
                    }
                }
                try {
                    executeCommand(cmd);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                System.out.print("$ ");
            }
        }
    }

    private static void packetShell(final String[] args) {
        StringBuilder commandsLine = new StringBuilder();
        for (int i = 0; i < args.length - 1; ++i) {
            commandsLine.append(args[i]);
            commandsLine.append(' ');
        }
        commandsLine.append(args[args.length - 1]);
        String[] commands = commandsLine.toString().split(";");
        for (int i = 0; i < commands.length; ++i) {
            try {
                executeCommand(commands[i]);
            } catch (IOException | IllegalArgumentException e) {
                System.err.println(e.getMessage());
                System.exit(-1);
            }
        }
    }

    private static void executeCommand(final String command) throws IOException {
        String[] argsWithName = command.trim().split(" ");
        for (String x : argsWithName) {
            x.trim();
        }
        String cmdName = argsWithName[0];
        LinkedList<String> argsList = new LinkedList<String>(Arrays.asList(argsWithName));
        argsList.remove(0);
        String[] args = new String[argsWithName.length - 1];
        argsList.toArray(args);
        switch (cmdName) {
            case "cd":
                CommandCd.execute(args);
                break;
            case "mkdir":
                CommandMkDir.execute(args);
                break;
            case "pwd":
                CommandPwd.execute();
                break;
            case "rm":
                CommandRm.execute(args);
                break;
            case "cp":
                CommandCp.execute(args);
                break;
            case "mv":
                CommandMv.execute(args);
                break;
            case "ls":
                CommandLs.execute(args);
                break;
            case "cat":
                CommandCat.execute(args);
                break;
            case "exit":
                System.exit(0);
                break;
            default:
                System.err.println("shell: wrong syntax");
                System.exit(-1);
                break;
        }
    }
}
