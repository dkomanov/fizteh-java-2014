package ru.fizteh.fivt.students.andrewzhernov.shell;

import java.util.Scanner;

public class Shell {
    public static void main(String[] args) {
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
    }

    public static void interactiveMode() {
        Scanner input = null;
        try {
            input = new Scanner(System.in);
            System.out.print("$ ");
            while (input.hasNextLine()) {
                try {
                    executeCommand(parseCommand(input.nextLine()));
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                System.out.print("$ ");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            System.out.println();
            input.close();
        }
    }

    public static void packageMode(String[] args) {
        String[] input = parseInput(args);
        int isError = 0;
        for (String cmd : input) {
            try {
                executeCommand(parseCommand(cmd));
            } catch (Exception e) {
                System.err.println(e.getMessage());
                isError = 1;
            }
        }
        System.exit(isError);
    }

    private static String[] parseInput(String[] args) {
        StringBuilder input = new StringBuilder();
        for (String cmd : args) {
            input.append(cmd).append(' ');
        }
        return input.toString().split("\\s*;\\s*");
    }

    private static String[] parseCommand(String cmd) {
        return cmd.split("\\s+");
    }

    private static void executeCommand(String[] cmd) throws Exception {
        if (cmd.length > 0 && cmd[0].length() > 0) {
            if (cmd[0].equals("cd")) {
                ChangeDir.execute(cmd);
            } else if (cmd[0].equals("mkdir")) {
                MakeDir.execute(cmd);
            } else if (cmd[0].equals("pwd")) {
                Pwd.execute(cmd);
            } else if (cmd[0].equals("rm")) {
                Remove.execute(cmd);
            } else if (cmd[0].equals("cp")) {
                Copy.execute(cmd);
            } else if (cmd[0].equals("mv")) {
                Move.execute(cmd);
            } else if (cmd[0].equals("ls")) {
                List.execute(cmd);
            } else if (cmd[0].equals("cat")) {
                Cat.execute(cmd);
            } else if (cmd[0].equals("exit")) {
                System.exit(0);
            } else {
                throw new Exception("Shell: " + cmd[0] + ": no such command");
            }
        }
    }

}
