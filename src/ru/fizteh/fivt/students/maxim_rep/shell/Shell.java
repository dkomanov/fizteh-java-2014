package ru.fizteh.fivt.students.maxim_rep.shell;

import java.io.*;

import ru.fizteh.fivt.students.maxim_rep.shell.commands.*;

public class Shell {

    public static String currentPath;

    public static void main(String[] args) throws IOException {
        currentPath = System.getProperty("user.home");

        if (args.length == 0) {
            Shell.interactiveMode();
        } else {
            Shell.commandMode(args);
        }
    }

    public static int commandMode(String[] args) throws IOException {
        String commandline = Parser.makeStringCommand(args);

        currentPath = System.getProperty("user.home");
        String[] commandsString = Parser.divideByChar(commandline, ";");
        for (String commandsString1 : commandsString) {
            ShellCommand command = Parser.getCommandFromString(commandsString1);
            if (!command.execute()) {
                System.exit(-1);
                return -1;
            }
        }
        System.exit(0);
        return 0;
    }

    public static void interactiveMode() throws IOException {
        currentPath = System.getProperty("user.home");

        while (true) {
            String convertedString = currentPath;
            if (currentPath.startsWith(System.getProperty("user.home"))) {
                convertedString = "~"
                        + currentPath.substring(System.getProperty("user.home")
                                .length());
            }
            System.out.print(System.getProperty("user.name") + "@"
                    + System.getProperty("os.name") + " " + convertedString
                    + " $ ");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            String line = in.readLine();

            String[] commandsString = Parser.divideByChar(line, ";");
            for (String commandsString1 : commandsString) {
                ShellCommand command = Parser
                        .getCommandFromString(commandsString1);
                if (command.getClass() == Exit.class) {
                    System.exit(0);
                }
                command.execute();
            }

        }
    }
}
