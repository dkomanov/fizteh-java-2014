package ru.fizteh.fivt.students.irina_karatsapova.proxy.client.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.client.InterpreterState;

import java.io.IOException;
import java.net.Socket;

public class ConnectCommand implements Command {
    public void execute(InterpreterState state, String[] args) throws Exception {
        if (state.isConnected()) {
            state.out.println("not connected: connection has already been established");
            return;
        }

        String host = args[1];
        int port;
        try {
            port = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            throw new Exception("Port should be a number");
        }
        Socket socket;
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            state.out.println("not connected: server is not listening there");
            return;
        }

        state.connect(host, port, socket);
        state.out.println("connected");
    }
    
    public String name() {
        return "connect";
    }
    
    public int minArgs() {
        return 3;
    }
    
    public int maxArgs() {
        return 3;
    }
}
