package ru.fizteh.fivt.students.anastasia_ermolaeva.shell;


import java.util.Scanner;
import java.util.NoSuchElementException;

public final class Shell {
    private Shell() {
        //
    }
    public static void main(final String[] args) {

    }
    public static void batchMode(final String[] args) {
        StringBuilder cmd = new StringBuilder();
        for (String arg: args) {
            cmd.append(arg);
            cmd.append(' ');
        }
        String[] commands = cmd.toString().trim().split(";");
        for (String command:commands) {
            commandHandler(command, false);
        }
    }
    private static void commandHandler(
            final String command, final boolean mode) {
        String[] arguments = command.split("\\s+");
        try {
            switch (arguments[0]) {
                case "cd":
                    Commands.cd(arguments);
                    break;
                case "mkdir":
                    Commands.mkdir(arguments);
                    break;
                case "pwd":
                    Commands.pwd(arguments);
                    break;
                case "rm":
                    Commands.rm(arguments);
                    break;
                case "cp":
                    Commands.cp(arguments);
                    break;
                case "mv":
                    Commands.mv(arguments);
                    break;
                case "ls":
                    Commands.ls(arguments);
                    break;
                case "cat":
                    Commands.cat(arguments);
                    break;
                case "exit":
                    Commands.exit(arguments);
                    break;
                default:
                    System.out.println(arguments[0] + ":unknown command ");
                    if (!mode) {
						System.exit(-1);
					}
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (!mode) {
                System.exit(-1);
            }
        }
    }
    public static void userMode() {
        try (Scanner scan = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                String line = "";
                try {
                    line = scan.nextLine();
                } catch (NoSuchElementException e) {
                    System.exit(0);
                }
                String[] commands = line.trim().split(";");
                for (String command:commands) {
                    commandHandler(command, true);
                }
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
