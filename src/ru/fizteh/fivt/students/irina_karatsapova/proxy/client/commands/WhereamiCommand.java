package ru.fizteh.fivt.students.irina_karatsapova.proxy.client.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.client.InterpreterState;

public class WhereamiCommand implements Command {
    public void execute(InterpreterState state, String[] args) {
        if (!state.connected) {
            state.out.println("local");
        } else {
            state.out.println("remote " + state.host + " " + state.port);
        }
    }
    
    public String name() {
        return "whereami";
    }
    
    public int minArgs() {
        return 1;
    }
    
    public int maxArgs() {
        return 1;
    }
}
