package ru.fizteh.fivt.students.irina_karatsapova.parallel.commands;

import ru.fizteh.fivt.students.irina_karatsapova.parallel.InterpreterState;

public interface Command {
    void execute(InterpreterState state, String[] args) throws Exception;
    
    String name();
    
    int minArgs();
    
    int maxArgs();
}

