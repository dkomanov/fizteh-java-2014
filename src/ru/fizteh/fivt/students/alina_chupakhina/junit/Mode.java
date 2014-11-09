package ru.fizteh.fivt.students.alina_chupakhina.junit;

import java.util.Scanner;

public class Mode {
    public static void interactive() {
        Scanner sc = new Scanner(System.in);
        try {
            while (true) {
                System.out.print("$ ");
                String [] s = sc.nextLine().trim().split(";");
                for (String command : s) {
                    Interpreter.doCommand(command, false);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void batch(final String[] args) {
        String arg;
        arg = args[0];
        for (int i = 1; i != args.length; i++) {
            arg = arg + ' ' + args[i];
        }
        String[] commands = arg.trim().split(";");
        try {
            for (int i = 0; i != commands.length; i++) {
                Interpreter.doCommand(commands[i], true);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
