package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

public class ExitCommand implements Command {
    public void execute(String[] args) {
        System.exit(0);
    }
    
    public String name() {
        return "exit";
    }
    
    public int minArgs() {
        return 1;
    }
    
    public int maxArgs() {
        return 1;
    }
}
