package ru.fizteh.fivt.students.valentine_lebedeva.filemap;

import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.ListCmd;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.PutCmd;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.ExitCmd;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.GetCmd;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.RemoveCmd;
import ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd.Cmd;

public final class Parser {
    private Map<String, Cmd> commands;

    public Parser() {
        commands = new HashMap<>(5);
        Cmd[] tmp = new Cmd[5];
        tmp[0] = new ListCmd();
        tmp[1] = new PutCmd();
        tmp[2] = new ExitCmd();
        tmp[3] = new GetCmd();
        tmp[4] = new RemoveCmd();
        for (int i = 0; i < 4; i++) {
            commands.put(tmp[i].getName(), tmp[i]);
        }
    }

    public void parse(final String cmdArgs,
            final Boolean cmdMode,
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
