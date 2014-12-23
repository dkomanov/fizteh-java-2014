package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.InterpreterStateServer;

public interface Command {
    void execute(InterpreterStateServer state, String[] args) throws Exception;
    
    String name();
    
    int minArgs();
    
    int maxArgs();
}

