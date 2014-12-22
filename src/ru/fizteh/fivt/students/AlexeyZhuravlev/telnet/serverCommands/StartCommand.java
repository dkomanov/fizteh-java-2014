package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.serverCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ServerLogic;

/**
 * @author AlexeyZhuravlev
 */
public class StartCommand extends ServerCommand {

    int port;

    @Override
    public void execute(ServerLogic base) throws Exception {
        try {
            base.start(port);
            System.out.println("started at " + port);
        } catch (Exception e) {
            throw new Exception("not started: " + e.getMessage());
        }
    }

    public void putArguments(String[] args) {
        if (args.length == 1) {
            port = 10001;
        } else {
            port = Integer.parseInt(args[1]);
        }
    }
}
