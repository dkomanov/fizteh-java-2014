package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.util.Scanner;

/**
 * @author AlexeyZhuravlev
 */

public class FileMapMain {
    public static void main(String[] args) {
        String path = System.getProperty("db.file");
        if (path == null) {
            System.err.println("No database file specified");
            System.exit(1);
        }
        try {
            DataBase base = new DataBase(path);
            if (args.length == 0) {
                interactiveMode(base);
            } else {
                packageMode(base, args);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }

    private static void interactiveMode(DataBase base) {
        Scanner scanner = new Scanner(System.in);
        boolean exitStatus = false;
        do {
            System.out.print("$ ");
            for (String s : scanner.nextLine().split(";\\s*")) {
                try {
                    Command newCommand = Command.fromString(s);
                    newCommand.execute(base);
                } catch (ExitCommandException e) {
                    exitStatus = true;
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.err.flush();
                }
            }
        } while (!exitStatus);
    }

    private static void packageMode(DataBase base, String[] args) throws Exception {
        StringBuilder allCommands = new StringBuilder();
        for (String s: args) {
            allCommands.append(s);
            allCommands.append(' ');
        }
        String[] commands = allCommands.toString().split(";\\s*", 0);
        for (String s: commands) {
            Command newCommand = Command.fromString(s);
            try {
                newCommand.execute(base);
            } catch (ExitCommandException e) {
                break;
            }
        }
    }
}
