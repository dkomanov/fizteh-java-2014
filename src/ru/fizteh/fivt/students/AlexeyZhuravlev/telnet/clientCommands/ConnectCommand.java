package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.clientCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ClientLogic;

import java.io.IOException;

/**
 * @author AlexeyZhuravlev
 */
public class ConnectCommand extends ClientCommand {

    String host;
    int port;

    @Override
    public void execute(ClientLogic base) throws Exception {
        if (base.isConnected()) {
            System.out.println("not connected: already connected");
        } else {
            try {
                base.connect(host, port);
                System.out.println("connected");
            } catch (IOException e) {
                System.out.println("not connected: " + e.getMessage());
            }
        }
    }

    @Override
    protected int numberOfArguments() {
        return 2;
    }

    @Override
    protected void putArguments(String[] args) {
        host = args[1];
        port = Integer.parseInt(args[2]);
    }
}
