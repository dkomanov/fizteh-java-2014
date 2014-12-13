package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands.server_commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.InterpreterStateServer;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands.Command;

import java.io.IOException;
import java.net.ServerSocket;

public class StartCommand implements Command {
    int port = 10001;

    public void execute(InterpreterStateServer state, String[] args) throws Exception {
        if (state.isStarted()) {
            state.out.println("not started: already started");
            return;
        }

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new Exception("Port should be a number");
            }
        }

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            state.out.println("not started: wrong port number or it is already busy");
            return;
        }

        state.start(port, serverSocket);
        state.out.println("started at port " + port);
    }
    
    public String name() {
        return "start";
    }
    
    public int minArgs() {
        return 1;
    }
    
    public int maxArgs() {
        return 2;
    }
}
