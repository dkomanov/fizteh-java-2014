package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands.server_commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.InterpreterStateServer;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands.Command;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.ClientAcceptorThread;

import java.util.List;

public class ListusersCommand implements Command {
    public void execute(InterpreterStateServer state, String[] args) {
        if (!state.isStarted()) {
            state.out.println("not started");
            return;
        }
        int port = state.getPort();
        List<ClientAcceptorThread> clientAcceptors = state.getClientAcceptors();
        for (ClientAcceptorThread clientAcceptor: clientAcceptors) {
            if  (clientAcceptor.isAlive()) {
                String host = clientAcceptor.clientSocket.getInetAddress().getHostName();
                state.out.println(host + ":" + port);
            }
        }
    }
    
    public String name() {
        return "listusers";
    }
    
    public int minArgs() {
        return 1;
    }
    
    public int maxArgs() {
        return 1;
    }
}
