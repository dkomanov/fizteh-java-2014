package ru.fizteh.fivt.students.isalysultan.FileMap;

import java.io.IOException;
import java.util.Scanner;

public class Interactive {

    private Interactive() {
        // Disable instantiation to this class.
    }

    public static void interactiveMode(Table tables) throws IOException {
        Scanner in = new Scanner(System.in);
        String[] parserCommand;
        Commands object = new Commands();
        CommandExecutor newParser = new CommandExecutor();
        while (true) {
            String command;
            System.out.print("$ ");
            command = in.nextLine();
            parserCommand = command.split(" ");
            newParser.execute(tables, parserCommand);
        }
    }
}
