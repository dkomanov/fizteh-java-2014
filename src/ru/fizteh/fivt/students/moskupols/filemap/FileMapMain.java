package ru.fizteh.fivt.students.moskupols.filemap;

import ru.fizteh.fivt.students.moskupols.cliutils.*;

import java.io.IOException;

/**
 * Created by moskupols on 26.09.14.
 */
public class FileMapMain {
    public static void main(String[] args) {
        String dbPath = System.getProperty("db.file");
        if (dbPath == null) {
            System.err.println("Specify database file in properties.");
            System.exit(1);
        }

        DataBase db = new DataBase();
        try {
            db.load(dbPath);
        } catch (Exception e) {
            System.err.println("Error while loading database:");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        CommandProcessor commandProcessor;
        if (args.length == 0) {
            commandProcessor = new InteractiveCommandProcessor();
        } else {
            commandProcessor = new PackageCommandProcessor(args);
        }

        try {
            commandProcessor.process(new FileMapCommandFabric(db));
        } catch (CommandExecutionException | UnknownCommandException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            try {
                db.dump(dbPath);
            } catch (IOException e) {
                System.err.println("Could not save database:");
                System.err.println(e.getMessage());
            }
        }
    }
}
