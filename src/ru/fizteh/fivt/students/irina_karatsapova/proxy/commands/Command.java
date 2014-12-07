package ru.fizteh.fivt.students.irina_karatsapova.proxy.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.InterpreterState;

public interface Command {
    void execute(InterpreterState state, String[] args) throws Exception;
    
    String name();
    
    int minArgs();
    
    int maxArgs();
}

