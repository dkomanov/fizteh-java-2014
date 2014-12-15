package ru.fizteh.fivt.students.moskupols.parallel;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.moskupols.cliutils.UnknownCommandException;
import ru.fizteh.fivt.students.moskupols.cliutils2.CommandChooser;
import ru.fizteh.fivt.students.moskupols.cliutils2.NameFirstCommandChooser;
import ru.fizteh.fivt.students.moskupols.cliutils2.commands.ExitCommand;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;
import ru.fizteh.fivt.students.moskupols.cliutils2.interpreters.BatchModeInterpreter;
import ru.fizteh.fivt.students.moskupols.cliutils2.interpreters.InteractiveInterpreter;
import ru.fizteh.fivt.students.moskupols.cliutils2.interpreters.Interpreter;
import ru.fizteh.fivt.students.moskupols.storeable.commands.*;

import java.io.IOException;

/**
 * Created by moskupols on 15.12.14.
 */
public class ParallelMain {
    public static final String DB_DIR_PROPERTY = "fizteh.db.dir";

    public static void main(String[] args) {
        String dbPath = System.getProperty(DB_DIR_PROPERTY);
        if (dbPath == null) {
            System.err.format("Specify database file in property %s.%n", DB_DIR_PROPERTY);
            System.exit(1);
        }

        final TableProviderFactory providerFactory = new ThreadSafeTableProviderFactory();
        final TableProvider provider;
        try {
            provider = providerFactory.create(dbPath);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }

        final StoreableContext context = new StoreableContextImpl(provider);
        final CommandChooser chooser = new NameFirstCommandChooser(
                new Commit(), new Create(), new Drop(),
                new Get(), new Put(), new Remove(),
                new Rollback(), new Size(), new Use(),
                new ShowTables(), new ExitCommand()
        );
        Interpreter interpreter;
        if (args.length == 0) {
            interpreter = new InteractiveInterpreter("$ ", context, chooser);
        } else {
            interpreter = new BatchModeInterpreter(context, chooser, args);
        }

        try {
            interpreter.interpret();
        } catch (CommandExecutionException | UnknownCommandException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
