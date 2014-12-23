package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.commands;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.InterpreterStateDatabase;

public interface DatabaseCommand {
    void execute(InterpreterStateDatabase state, String[] args) throws Exception;
    
    String name();
    
    int minArgs();
    
    int maxArgs();
}

