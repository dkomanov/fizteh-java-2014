package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.serverCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ServerLogic;

/**
 * @author AlexeyZhuravlev
 */
public class StopCommand extends ServerCommand {
    @Override
    public void execute(ServerLogic base) throws Exception {
        if (base.isStarted()) {
            int port = base.stop();
            System.out.println("stopped at " + port);
        } else {
            System.out.println("not started");
        }
    }
}
