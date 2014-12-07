package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import java.util.Scanner;

/**
 * Created by Мирон on 07.12.2014 ru.fizteh.fivt.students.LevkovMiron.Tellnet.
 */
public class ClientServerLogic {

    private int applicationType = -1;
    ConsoleApp consoleApp = new ConsoleApp(-1);

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] commands = input.split(";");
        for (String cmd : commands) {
            consoleApp.runCommand(cmd.trim());
        }
        run();
    }
}
