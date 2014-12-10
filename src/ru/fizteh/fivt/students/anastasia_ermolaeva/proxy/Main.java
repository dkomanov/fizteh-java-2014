package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.commands.CommandsPackage;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.Utility;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.DatabaseFormatException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.ExitException;

import java.io.IOException;

public class Main {
    private static final String DATABASE_DIRECTORY = "fizteh.db.dir";

    public static void main(final String[] args) {
        String rootDirectory = System.getProperty(DATABASE_DIRECTORY);
        if (rootDirectory == null) {
            System.err.println("You must specify fizteh.db.dir via JVM parameter");
            System.exit(1);
        }
        TableProviderFactory factory = new TableHolderFactory();
        try {
            start((TableHolder) factory.create(rootDirectory), args);
        } catch (DatabaseFormatException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException io) {
            System.err.println(Utility.IO_MSG + io.getMessage());
            System.exit(1);
        } catch (ExitException t) {
            System.exit(t.getStatus());
        }
    }

    private static void start(TableHolder tableHolder, final String[] args) throws ExitException {
        new Interpreter(new CommandsPackage(tableHolder).pack).run(args);
    }
}
