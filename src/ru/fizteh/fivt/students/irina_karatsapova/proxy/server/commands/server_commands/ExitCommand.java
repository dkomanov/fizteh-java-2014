package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands.server_commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.InterpreterStateServer;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands.Command;

public class ExitCommand implements Command {
    public void execute(InterpreterStateServer state, String[] args) throws Exception {
        if (state.started) {
            state.stop();
        }
        System.exit(0);
    }
    
    public String name() {
        return "exit";
    }
    
    public int minArgs() {
        return 1;
    }
    
    public int maxArgs() {
        return 1;
    }
}
