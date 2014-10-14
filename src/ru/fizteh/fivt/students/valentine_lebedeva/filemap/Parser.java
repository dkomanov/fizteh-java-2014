package ru.fizteh.fivt.students.valentine_lebedeva.filemap;

import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.Command;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.ExitCommand;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.GetCommand;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.ListCommand;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.PutCommand;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.RemoveCommand;

public final class Parser {
    private Map<String, Command> commands;

    public Parser() {
        commands = new HashMap<>();
        commands.put("list", new ListCommand());
        commands.put("put", new PutCommand());
        commands.put("exit", new ExitCommand());
        commands.put("get", new GetCommand());
        commands.put("remove", new RemoveCommand());
    }

    public void parse(final String cmdArgs, final Boolean cmdMode,
            final DB dataBase) throws Exception {
        try {
            String[] parseArgs = cmdArgs.split(" ");
            if (commands.get(parseArgs[0]) != null) {
                commands.get(parseArgs[0]).execute(dataBase, parseArgs);
            } else {
                throw new IllegalArgumentException("Wrong arguments");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (cmdMode) {
                System.exit(1);
            }
        }
    }
}
