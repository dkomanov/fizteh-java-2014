package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

import java.io.File;

public class LsCommand implements Command {
    public void execute(String[] args) {
        String[] contents = new File(Utils.currentPath()).list();
        for (String content: contents) {
            System.out.println(content);
        }
    }
    
    public String name() {
        return "ls";
    }
    
    public int minArgs() {
        return 1;
    }
    
    public int maxArgs() {
        return 1;
    }
}
