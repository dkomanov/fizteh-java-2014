/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell;

import java.util.Scanner;

/**
 *
 * @author shakarim
 */
public class ShellInterface {

    private static String[] parseArgs(String[] args) {
        String command;
        StringBuilder newArgs = new StringBuilder();

        for (String str : args) {
            newArgs.append(str).append(' ');
        }

        command = newArgs.toString();

        return command.split("\\s*;\\s*");
    }

    private static String[] parseCommand(String command) {
        return command.split("\\s+");
    }

    private static void switchCommand(String[] cmd) throws Exception {
        if (cmd.length > 0 && cmd[0].length() > 0) {
            switch (cmd[0]) {
                case "cat":
                    CatCommand.run(cmd);
                    break;
                case "cd":
                    CdCommand.run(cmd);
                    break;
                case "cp":
                    CpCommand.run(cmd);
                    break;
                case "ls":
                    LsCommand.run(cmd);
                    break;
                case "mkdir":
                    MkdirCommand.run(cmd);
                    break;
                case "mv":
                    MvCommand.run(cmd);
                    break;
                case "pwd":
                    PwdCommand.run(cmd);
                    break;
                case "rm":
                    RmCommand.run(cmd);
                    break;
                case "exit":
                    System.exit(0);
                default:
                    throw new Exception("Shell : '"
                            + cmd[0] + "' No such command");
            }
        }
    }

    public static void packMode(String[] args) {
        String[] newArgs = parseArgs(args);
        int onExit = 0;

        for (String command : newArgs) {
            try {
                switchCommand(parseCommand(command));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                onExit = 1;
            }
        }
        System.exit(onExit);
    }

    public static void interMode() {
        Scanner input = null;
        try {
            input = new Scanner(System.in);
            System.out.print("$ ");
            while (input.hasNextLine()) {
                try {
                    String cmd = input.nextLine();
                    switchCommand(parseCommand(cmd));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                System.out.print("$ ");
            }
        } finally {
            if (input != null) {
                System.out.println();
                input.close();
            }
        }
    }
}
