package ru.fizteh.fivt.students.kolmakov_sergey.multi_file_map;

import java.nio.file.InvalidPathException;

public class DatabaseMain {
    public static void main(String[] args) {
        String rootDirectory = System.getProperty("fizteh.db.dir");
        if (rootDirectory == null) {
            System.err.println("You must specify db.file via -Ddb.file JVM parameter");
            System.exit(-1);
        }
        try {
            TableManager manager = new TableManager(rootDirectory);
            CommandInterpreter.setManager(manager);
            if (args.length == 0) {
                Parser.interactiveMode();
            } else {
                Parser.batchMode(args);
            }
        } catch (InvalidPathException e) {
            System.err.println("Can't connect to database: invalid path");
            System.exit(-1);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().isEmpty()) {
                System.err.println(e.getMessage());
            } else {
                System.err.println("Unexpected exception:");
                e.printStackTrace();
                System.exit(-1);
            }
        } catch (DatabaseExitException e) {
            System.exit(e.status);
        }
    }
}

class DatabaseExitException extends Exception {
    public final int status;
    public DatabaseExitException(int s) {
        status = s;
    }
}

class WrongNumberOfArgumentsException extends IllegalArgumentException {
    WrongNumberOfArgumentsException(String message) {
        super(message);
    }
}

