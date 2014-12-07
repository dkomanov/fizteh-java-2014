package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.serverCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ServerLogic;

/**
 * @author AlexeyZhuravlev
 */
public class StopCommand extends ServerCommand {
    @Override
    public void execute(ServerLogic base) throws Exception {
        if (base.isStarted()) {
            base.stop();
        } else {
            System.out.println("not started");
        }
    }
}
