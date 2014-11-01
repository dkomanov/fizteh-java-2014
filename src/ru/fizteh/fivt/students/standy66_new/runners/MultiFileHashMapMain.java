package ru.fizteh.fivt.students.standy66_new.runners;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.standy66_new.Interpreter;
import ru.fizteh.fivt.students.standy66_new.commands.Command;
import ru.fizteh.fivt.students.standy66_new.commands.ExitCommand;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringDatabase;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringDatabaseFactory;
import ru.fizteh.fivt.students.standy66_new.storage.strings.commands.CommandFactory;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by astepanov on 20.10.14.
 */
public class MultiFileHashMapMain {
    public static void main(String[] args) {
        String dbDir = System.getProperty("fizteh.db.dir");
        if (dbDir == null) {
            System.err.println("No dir specified, use -Dfizteh.db.dir=...");
            System.exit(1);
        }
        TableProviderFactory tableProviderFactory = new StringDatabaseFactory();
        TableProvider provider = tableProviderFactory.create(dbDir);
        CommandFactory commandFactory = new CommandFactory(provider);
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
            ((StringDatabase) provider).commit();
        }
    }
}
