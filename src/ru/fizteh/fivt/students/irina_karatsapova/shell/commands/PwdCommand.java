package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

public class PwdCommand implements Command {
    public void execute(String[] args) {
        System.out.println(Utils.currentPath());
    }
    
    public String name() {
        return "pwd";
    }
    
    public int minArgs() {
        return 1;
    }
    
    public int maxArgs() {
        return 1;
    }
}
