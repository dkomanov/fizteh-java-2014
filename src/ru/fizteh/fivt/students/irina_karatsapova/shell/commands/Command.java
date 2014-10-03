package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

public interface Command {
    public void execute(String[] args) throws Exception;
    
    public String name();
    
    public int minArgs();
    
    public int maxArgs();
}

