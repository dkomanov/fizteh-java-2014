package ru.fizteh.fivt.students.irina_karatsapova.proxy.client.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.client.InterpreterState;

public interface Command {
    void execute(InterpreterState state, String[] args) throws Exception;
    
    String name();
    
    int minArgs();
    
    int maxArgs();
}

