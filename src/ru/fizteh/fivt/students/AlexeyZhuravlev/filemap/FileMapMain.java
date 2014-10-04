package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

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
        AtomicBoolean exitStatus = new AtomicBoolean(false);
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.print("$ ");
            for (String s : scanner.nextLine().split(";")) {
                try {
                    Command newCommand = Command.fromString(s);
                    newCommand.execute(base, exitStatus);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.err.flush();
                }
                if (exitStatus.get()) {
                    break;
                }
            }
        } while (!exitStatus.get());
    }

    private static void packageMode(DataBase base, String[] args) throws Exception {
        StringBuilder allCommands = new StringBuilder();
        for (String s: args) {
            allCommands.append(s);
            allCommands.append(' ');
        }
        String[] commands = allCommands.toString().split(";");
        for (String s: commands) {
            AtomicBoolean exitStatus = new AtomicBoolean(false);
            Command newCommand = Command.fromString(s);
            newCommand.execute(base, exitStatus);
            if (exitStatus.get()) {
                break;
            }
        }
    }
}
