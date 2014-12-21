package ru.fizteh.fivt.students.standy66_new.runners;

import ru.fizteh.fivt.students.standy66_new.Interpreter;
import ru.fizteh.fivt.students.standy66_new.commands.Command;
import ru.fizteh.fivt.students.standy66_new.commands.CommandFactory;
import ru.fizteh.fivt.students.standy66_new.commands.ExitCommand;
import ru.fizteh.fivt.students.standy66_new.server.commands.ServerCommandFactory;
import ru.fizteh.fivt.students.standy66_new.server.http.HttpDbServer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class Task9Runner {
    public static void main(String... args) throws Exception {
        String dbDir = System.getProperty("fizteh.db.dir");
        if (dbDir == null) {
            System.err.println("No dir specified, use -Dfizteh.db.dir=...");
            System.exit(1);
        }
        PrintWriter systemOutWriter = new PrintWriter(System.out, true);
        File dbDirFile = new File(dbDir);
        CommandFactory serverCommandFactory = new ServerCommandFactory(systemOutWriter,
                new HttpDbServer(dbDirFile), null);
        Map<String, Command> availableCommands = serverCommandFactory.getCommandMap("en-US");
        availableCommands.put("exit", new ExitCommand(systemOutWriter));
        Interpreter interpreter;
        if (args.length == 0) {
            interpreter = new Interpreter(System.in, availableCommands, true);
        } else {
            String params = Stream.of(args).collect(Collectors.joining(" "));
            interpreter = new Interpreter(new ByteArrayInputStream(params.getBytes()), availableCommands, false);
        }
        interpreter.execute();


    }
}
