package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

import java.io.File;

public class CatCommand implements Command {
    public void execute(String[] args) throws Exception {
        File catted = Utils.makePathAbsolute(args[1]).toFile();

        Utils.checkExistance(catted);
        Utils.checkFile(catted);

        Utils.print(catted);
    }
    
    public String name() {
        return "cat";
    }
    
    public int minArgs() {
        return 2;
    }
    
    public int maxArgs() {
        return 2;
    }
}

