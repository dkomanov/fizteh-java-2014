package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

import java.io.File;

public class RmCommand implements Command {
    public void execute(String[] args) throws Exception {
        if (!args[1].equals("-r")) {
            File removed = Utils.makePathAbsolute(args[1]).toFile();
            Utils.checkExistance(removed);
            if ((removed.isFile()) || ((removed.isDirectory()) && (removed.list().length == 0))) {
                Utils.delete(removed);
            }
        } else {
            File removed = Utils.makePathAbsolute(args[2]).toFile();
            Utils.checkExistance(removed);
            recursiveRemove(removed);
        }
    }
    
    public void recursiveRemove(File removed) throws Exception {
        if (removed.isDirectory()) {
            for (File object: removed.listFiles()) {
                recursiveRemove(object);
            }
        }
        Utils.delete(removed);
    }
    
    public String name() {
        return "rm";
    }
    
    public int minArgs() {
        return 2;
    }
    
    public int maxArgs() {
        return 3;
    }
}
