package ru.fizteh.fivt.students.irina_karatsapova.proxy.client.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.client.InterpreterState;

public class DisconnectCommand implements Command {
    public void execute(InterpreterState state, String[] args) throws Exception {
        if (!state.connected) {
            state.out.println("not connected");
            return;
        }
        state.disconnect();
        state.out.println("disconnected");
    }
    
    public String name() {
        return "disconnect";
    }
    
    public int minArgs() {
        return 1;
    }
    
    public int maxArgs() {
        return 1;
    }
}
