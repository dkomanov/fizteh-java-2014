package ru.fizteh.fivt.students.maxim_rep.database;

import ru.fizteh.fivt.students.maxim_rep.database.commands.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DbMain {
    static String filePath;

    public static void main(String[] args) throws IOException {
        DbMain.filePath = System.getProperty("db.file");
        if (!IoLib.writeToDB(filePath, "", true)) {
            System.exit(-1);
        }
        if (args.length == 0) {
            interactiveMode();
        } else {
            commandMode(args);
        }
    }

    public static int commandMode(String[] args) throws IOException {
        String commandline = Parser.makeStringCommand(args);

        String[] commandsString = Parser.divideByChar(commandline, ";");
        for (String commandsString1 : commandsString) {
            DBCommand command = Parser.getCommandFromString(commandsString1);
            if (!command.execute()) {
                System.exit(-1);
                return -1;
            }
        }
        System.exit(0);
        return 0;
    }

    public static void interactiveMode() throws IOException {

        while (true) {
            System.out.print(">>");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            String line = in.readLine();

            String[] commandsString = Parser.divideByChar(line, ";");
            for (String commandsString1 : commandsString) {
                DBCommand command = Parser
                        .getCommandFromString(commandsString1);
                if (command.getClass() == Exit.class) {
                    System.exit(0);
                }
                command.execute();
            }

        }
    }
}
