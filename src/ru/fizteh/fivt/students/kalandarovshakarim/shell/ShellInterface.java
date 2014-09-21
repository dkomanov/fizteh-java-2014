/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell;

import java.io.InputStream;

/**
 *
 * @author shakarim
 */
public class ShellInterface {

    private static String readStr(InputStream in) throws Exception {
        byte[] buff = new byte[4096];
        int length = 0;

        try {
            length = System.in.read(buff);
        } catch (Exception e) {
            throw new Exception("Shell: cannot read from stdin");
        }

        String retVal = new String(buff, 0, length - 1);

        return retVal;
    }

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
                LsCommand.run();
                break;
            case "mkdir":
                MkdirCommand.run(cmd);
                break;
            case "mv":
                MvCommand.run(cmd);
                break;
            case "pwd":
                PwdCommand.run();
                break;
            case "rm":
                RmCommand.run(cmd);
                break;
            case "exit":
                System.exit(0);
            default:
                if (cmd[0].length() > 0) {
                    throw new Exception("Shell : "
                            + cmd[0] + " No such command");
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
        do {
            System.out.print("$ ");
            try {
                String cmd;
                cmd = readStr(System.in);
                switchCommand(parseCommand(cmd));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } while (true);
    }
}
