package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

public interface Command {
    void execute(String[] args) throws Exception;
    
    String name();
    
    int minArgs();
    
    int maxArgs();
}

