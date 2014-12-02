package ru.fizteh.fivt.students.maxim_rep.multifilehashmap;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class DbMain {
    public static String databaseFilePath;
    public static TableDataMap fileStoredStringMap;

    public static boolean databaseExists(String filePath) {
        File file;
        file = new File(filePath);
        if (!file.exists() || file.isFile()) {
            return false;
        }
        return true;
    }

    public static String getCurrentTableName() {
        if (fileStoredStringMap != null) {
            return fileStoredStringMap.getTableName();
        } else {
            return null;
        }
    }

    public static String getTablePath(String tableName) {
        return databaseFilePath + System.getProperty("file.separator")
                + tableName;
    }

    public static void main(String[] args) throws IOException {
        databaseFilePath = System.getProperty("fizteh.db.dir");
        if (databaseFilePath == null) {
            System.out.println("Use -Dfizteh.db.dir=\"dir\" in JVM parameter");
            System.exit(-1);
        }

        if (!databaseExists(databaseFilePath)) {
            System.out
                    .println("Database error: Database folder doesn't exists!");
            System.exit(-1);
        }
        if (args.length == 0) {
            System.exit(interactiveMode());
        } else {
            System.exit(commandMode(args));
        }
    }

    private static int commandMode(String[] args) throws IOException {
        String commandline = Parser.makeStringCommand(args);

        if (commandline == null) {
            System.out.println();
            System.exit(0);
        }

        String[] commandsString = Parser.divideByChar(commandline, ";");
        for (String commandsString1 : commandsString) {
            DBCommand command = Parser.getCommandFromString(commandsString1);

            if (command.getClass() == Exit.class) {
                command.execute();
                return 0;
            }

            if (!command.execute()) {
                System.exit(-1);
                return -1;
            }
        }
        return 0;
    }

    private static int interactiveMode() throws IOException {

        while (true) {
            System.out.print("$ ");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            String line = in.readLine();
            if (line == null) {
                System.err.println("Database Error: Wrong input");
                return 0;
            }

            String[] commandsString = Parser.divideByChar(line, ";");
            for (String commandsString1 : commandsString) {
                DBCommand command = Parser
                        .getCommandFromString(commandsString1);
                if (command.getClass() == Exit.class) {
                    command.execute();
                    return 0;
                }
                command.execute();
            }

        }
    }
}
