package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands.server_commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.InterpreterStateServer;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands.Command;

public class StopCommand implements Command {
    public void execute(InterpreterStateServer state, String[] args) throws Exception {
        if (!state.started) {
            state.out.println("not started");
            return;
        }

        int port = state.stop();
        state.out.println("stopped at port " + port);
    }
    
    public String name() {
        return "stop";
    }
    
    public int minArgs() {
        return 1;
    }
    
    public int maxArgs() {
        return 1;
    }
}
