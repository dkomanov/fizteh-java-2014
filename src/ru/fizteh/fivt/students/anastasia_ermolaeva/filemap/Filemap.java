package ru.fizteh.fivt.students.anastasia_ermolaeva.filemap;

import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Filemap {
    private Filemap() {
        //
    }

    public static void main(final String[] args) throws Exception {
    }

    private static void commandHandler(final DbOperations db,
                                       final String command, final boolean mode) throws ExitException {
        String[] arguments = command.trim().split("\\s+");
        try {
            if (arguments.length > 0 && !arguments[0].isEmpty()) {
                switch (arguments[0]) {
                    case "put":
                        Commands.put(db, arguments);
                        break;
                    case "remove":
                        Commands.remove(db, arguments);
                        break;
                    case "list":
                        Commands.list(db, arguments);
                        break;
                    case "get":
                        Commands.get(db, arguments);
                        break;
                    case "exit":
                        if (Commands.exit(db, arguments)) {
                            throw new ExitException(0);
                        } else {
                            System.out.println("exit: too much arguments");
                            throw new ExitException(-1);
                        }
                    default:
                        System.out.println(arguments[0] + ": unknown command");
                        if (!mode) {
                            System.exit(-1);
                        }
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            if (!mode) {
                throw new ExitException(-1);
            }
        }
    }

    public static void userMode(
            final DbOperations db) throws ExitException {
        db.create();
        try (Scanner scan = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                String line = "";
                try {
                    line = scan.nextLine();
                } catch (NoSuchElementException e) {
                    System.exit(0);
                }
                String[] commands = line.trim().split(";");
                for (String command : commands) {
                    commandHandler(db, command, true);
                }
            }
        } catch (NoSuchElementException e) {
            throw new ExitException(-1);
        }
    }

    public static void batchMode(
            final DbOperations db, final String[] args) throws ExitException {
        db.create();
        StringBuilder cmd = new StringBuilder();
        for (String arg : args) {
            cmd.append(arg);
            cmd.append(' ');
        }
        String[] commands = cmd.toString().trim().split(";");
        for (String command : commands) {
            commandHandler(db, command, false);
        }
    }
}

class ExitException extends Exception {
    private final int status;

    public int getStatus() {
        return status;
    }

    public ExitException(int exitStatus) {
        status = exitStatus;
    }
}
