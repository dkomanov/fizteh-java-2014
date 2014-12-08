package ru.fizteh.fivt.students.moskupols.junit;

import ru.fizteh.fivt.students.moskupols.cliutils.*;

/**
 * Created by moskupols on 17.11.14.
 */
public class JUnitMain {
    public static final String DB_DIR_PROPERTY = "fizteh.db.dir";

    public static void main(String[] args) {
        String dbPath = System.getProperty(DB_DIR_PROPERTY);
        if (dbPath == null) {
            System.err.println(String.format("Specify database file in property %s.", DB_DIR_PROPERTY));
            System.exit(1);
        }

        final MultiFileMapTableProviderFactory providerFactory = new MultiFileMapTableProviderFactory();
        final JUnitCommandFactory commandFactory;
        try {
            commandFactory = new JUnitCommandFactory(providerFactory.create(dbPath));
        } catch (IllegalArgumentException e) {
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
