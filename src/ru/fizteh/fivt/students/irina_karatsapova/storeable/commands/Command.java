package ru.fizteh.fivt.students.irina_karatsapova.storeable.commands;

import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProvider;

public interface Command {
    void execute(TableProvider tableProvider, String[] args) throws Exception;
    
    String name();
    
    int minArgs();
    
    int maxArgs();
}

