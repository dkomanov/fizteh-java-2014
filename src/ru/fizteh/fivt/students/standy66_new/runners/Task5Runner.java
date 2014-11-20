package ru.fizteh.fivt.students.standy66_new.runners;

import ru.fizteh.fivt.students.standy66_new.Interpreter;
import ru.fizteh.fivt.students.standy66_new.commands.Command;
import ru.fizteh.fivt.students.standy66_new.commands.ExitCommand;
import ru.fizteh.fivt.students.standy66_new.storage.strings.commands.CommandFactory;
import ru.fizteh.fivt.students.standy66_new.storage.structured.StructuredDatabase;
import ru.fizteh.fivt.students.standy66_new.storage.structured.StructuredDatabaseFactory;
import ru.fizteh.fivt.students.standy66_new.storage.structured.commands.ExtendedCommandFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by andrew on 14.11.14.
 */
public class Task5Runner {
    public static void main(String[] args) {
        System.setProperty("warn_unsaved", "true");
        String dbDir = System.getProperty("fizteh.db.dir");
        if (dbDir == null) {
            System.err.println("No dir specified, use -Dfizteh.db.dir=...");
            System.exit(1);
        }
        StructuredDatabaseFactory tableProviderFactory = new StructuredDatabaseFactory();
        StructuredDatabase provider = null;
        try {
            provider = (StructuredDatabase) tableProviderFactory.create(dbDir);
        } catch (IOException e) {
            System.err.println("Couldn't create TableProvider");
            System.exit(-1);
        }
        CommandFactory commandFactory = new ExtendedCommandFactory(provider);
        Map<String, Command> availableCommands = commandFactory.getCommandsMap("en-US");
        availableCommands.put("exit", new ExitCommand());
        Interpreter interpreter;
        if (args.length == 0) {
            interpreter = new Interpreter(System.in, availableCommands, true);
        } else {
            String params = Stream.of(args).collect(Collectors.joining(" "));
            interpreter = new Interpreter(new ByteArrayInputStream(params.getBytes()), availableCommands, false);
        }
        if (interpreter.run()) {
            provider.getBackendDatabase().commit();
            provider.getBackendDatabase().close();
        }
    }
}
