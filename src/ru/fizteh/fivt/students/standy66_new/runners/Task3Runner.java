package ru.fizteh.fivt.students.standy66_new.runners;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.standy66_new.Interpreter;
import ru.fizteh.fivt.students.standy66_new.commands.Command;
import ru.fizteh.fivt.students.standy66_new.commands.ExitCommand;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringDatabase;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringDatabaseFactory;
import ru.fizteh.fivt.students.standy66_new.storage.strings.commands.CommandFactory;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by astepanov on 20.10.14.
 */
public final class Task3Runner {
    private Task3Runner() {
    }

    public static void main(String... args) {
        String dbDir = System.getProperty("fizteh.db.dir");
        if (dbDir == null) {
            System.err.println("No dir specified, use -Dfizteh.db.dir=...");
            System.exit(1);
        }
        TableProviderFactory tableProviderFactory = new StringDatabaseFactory();
        StringDatabase provider = null;
        try {
            provider = (StringDatabase) tableProviderFactory.create(dbDir);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        PrintWriter systemOutWriter = new PrintWriter(System.out, true);
        CommandFactory commandFactory = new CommandFactory(systemOutWriter, provider);
        Map<String, Command> availableCommands = commandFactory.getCommandsMap();
        availableCommands.put("exit", new ExitCommand(systemOutWriter));
        Interpreter interpreter;
        if (args.length == 0) {
            interpreter = new Interpreter(System.in, availableCommands, true);
        } else {
            String params = Stream.of(args).collect(Collectors.joining(" "));
            interpreter = new Interpreter(new ByteArrayInputStream(params.getBytes()), availableCommands, false);
        }
        if (interpreter.execute()) {
            try {
                provider.commit();
                provider.close();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
