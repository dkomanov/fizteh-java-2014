package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.clientCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ClientLogic;

/**
 * @author AlexeyZhuravlev
 */
public class DisconnectCommand extends ClientCommand {
    @Override
    public void execute(ClientLogic base) throws Exception {
        if (!base.isConnected()) {
            System.out.println("not connected");
        } else {
            base.disconnect();
            System.out.println("disconnected");
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
