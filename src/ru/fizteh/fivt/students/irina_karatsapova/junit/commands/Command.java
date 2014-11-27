package ru.fizteh.fivt.students.irina_karatsapova.junit.commands;

import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProvider;

public interface Command {
    void execute(TableProvider tableProvider, String[] args) throws Exception;
    
    String name();
    
    int minArgs();
    
    int maxArgs();
}

