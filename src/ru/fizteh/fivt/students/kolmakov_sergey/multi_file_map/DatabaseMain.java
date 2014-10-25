package ru.fizteh.fivt.students.kolmakov_sergey.multi_file_map;

import java.nio.file.InvalidPathException;

public class DatabaseMain {
    public static void main(String[] args) {
        String rootDirectory = System.getProperty("fizteh.db.dir");
        if (rootDirectory == null) {
            System.out.println("You must specify db.file via -Ddb.file JVM parameter");
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
        } catch (DatabaseCorruptedException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        } catch (InvalidPathException e) {
            System.out.println("Can't connect to database: invalid path");
            System.exit(-1);
        } catch (DatabaseExitException e) {
            if (!(e.getMessage() == null) && !e.getMessage().isEmpty()) {
                System.out.println(e.getMessage());
            }
            System.exit(e.status);
        }
    }
}

class DatabaseExitException extends Exception {
    public final int status;
    public DatabaseExitException(int status, Exception e) {
        super(e);
        this.status = status;
    }
}

class WrongNumberOfArgumentsException extends Exception {
    WrongNumberOfArgumentsException(String message) {
        super(message);
    }
}

class DatabaseCorruptedException extends Exception {
    DatabaseCorruptedException (String message) {
        super(message);
    }
}

class WrongNameException extends Exception {
    WrongNameException (String message) {
        super(message);
    }
}


