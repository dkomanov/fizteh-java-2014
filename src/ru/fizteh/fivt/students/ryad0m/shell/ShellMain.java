package ru.fizteh.fivt.students.ryad0m.shell;

import java.util.Scanner;

public class ShellMain {

    static Runner runner = new Runner();


    public static void runLine(String line) {
        String[] commands = line.split(";");
        for (String command_i : commands) {
            String command = command_i.trim().replaceAll("\\s+", " ");
            if (!command.equals(""))
                runner.runCommand(command.split(" "));
        }
    }

    public static void interactive() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("$ ");
        System.out.flush();
        while (scanner.hasNextLine()) {
            runLine(scanner.nextLine());
            System.out.print("$ ");
            System.out.flush();


        }
    }

    public static void runArgs(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg);
            sb.append(' ');
        }
        runLine(sb.toString());
    }

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                interactive();
            } else {
                runArgs(args);
            }
        } catch (Exception e) {
            System.out.println("Unhandled error: " + e.getMessage());
        }

    }
}
