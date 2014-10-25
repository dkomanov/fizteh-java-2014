package ru.fizteh.fivt.students.isalysultan.MultiFileHashMap;

import java.io.IOException;
import java.util.Scanner;

public class InteractiveMode {

    public static void interactive(RootDirectory direct) throws IOException {
        Scanner in = new Scanner(System.in);
        String[] parserCommand;
        CommandInterpretator newParser = new CommandInterpretator();
        while (true) {
            String command;
            System.out.print("$ ");
            command = in.nextLine();
            parserCommand = command.split(" ");
            newParser.executeCommand(direct, parserCommand);
        }
    }
}
