package ru.fizteh.fivt.students.moskupols.multifilehashmap;

import ru.fizteh.fivt.students.moskupols.cliutils.*;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by moskupols on 19.10.14.
 */
public class MultiFileMapMain {
    public static final String DB_DIR_PROPERTY = "fizteh.db.dir";

    public static void main(String[] args) {
        String dbPath = System.getProperty(DB_DIR_PROPERTY);
        if (dbPath == null) {
            System.err.println(String.format("Specify database file in property %s.", DB_DIR_PROPERTY));
            System.exit(1);
        }

        final MultiFileHashMapCommandFactory commandFactory;
        try {
            commandFactory = new MultiFileHashMapCommandFactory(
                    new DataBaseCursor(),
                    new MultiFileMapProvider(Paths.get(dbPath)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        CommandProcessor commandProcessor;
        if (args.length == 0) {
            commandProcessor = new InteractiveCommandProcessor("$ ");
        } else {
            commandProcessor = new PackageCommandProcessor(args);
        }

        try {
            commandProcessor.process(commandFactory);
        } catch (CommandExecutionException | UnknownCommandException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
