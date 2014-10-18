package ru.fizteh.fivt.students.maxim_rep.multifilehashmap;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DbMain {
    public static String filePath;
    public static String currentTable = null;

    public static void main(String[] args) throws IOException {
        DbMain.filePath = System.getProperty("fizteh.db.dir");

        if (!IoLib.databaseExists(DbMain.filePath)) {
            System.exit(-1);
        }
        if (args.length == 0) {
            System.exit(interactiveMode());
        } else {
            System.exit(commandMode(args));
        }
    }

    public static int commandMode(String[] args) throws IOException {
        String commandline = Parser.makeStringCommand(args);

        if (commandline.equals(null)) {
            System.out.println();
            System.exit(0);
        }

        String[] commandsString = Parser.divideByChar(commandline, ";");
        for (String commandsString1 : commandsString) {
            DBCommand command = Parser.getCommandFromString(commandsString1);
            if (!command.execute()) {
                System.exit(-1);
                return -1;
            }
        }
        return 0;
    }

    public static int interactiveMode() throws IOException {

        while (true) {
            System.out.print(">>");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            String line = in.readLine();

            if (line.equals(null)) {
                System.out.println();
            }

            String[] commandsString = Parser.divideByChar(line, ";");
            for (String commandsString1 : commandsString) {
                DBCommand command = Parser
                        .getCommandFromString(commandsString1);
                if (command.getClass() == Exit.class) {
                    return 0;
                }
                command.execute();
            }

        }
    }
}
