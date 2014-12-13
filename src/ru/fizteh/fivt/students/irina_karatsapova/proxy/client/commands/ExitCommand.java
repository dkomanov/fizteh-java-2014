package ru.fizteh.fivt.students.irina_karatsapova.proxy.client.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.client.InterpreterState;

public class ExitCommand implements Command {
    public void execute(InterpreterState state, String[] args) throws Exception {
        if (!state.isConnected()) {
            System.exit(0);
        }
        state.disconnect();
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
