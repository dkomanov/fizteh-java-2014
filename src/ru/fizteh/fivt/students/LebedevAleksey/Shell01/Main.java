package ru.fizteh.fivt.students.LebedevAleksey.Shell01;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Shell shell = new Shell();
        if (args.length == 0) {
            Scanner scanner = new Scanner(System.in);
            String command;
            do {
                System.out.print("$ ");
                command = scanner.nextLine();
                shell.invokeCommand(command);
            } while (!shell.isCorrectTerminated());
        }
        if (!shell.isCorrectTerminated())
            System.exit(1);
    }
}
