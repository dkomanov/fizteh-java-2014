package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

import java.io.File;

public class CpCommand implements Command {
    public void execute(String[] args) throws Exception {
        File copied, destination;
        if (!args[1].equals("-r")) {
            copied = Utils.makePathAbsolute(args[1]).toFile();
            destination = Utils.makePathAbsolute(args[2]).toFile();
        } else {
            copied = Utils.makePathAbsolute(args[2]).toFile();
            destination = Utils.makePathAbsolute(args[3]).toFile();
        }
        
        File newDestination = new File(destination, copied.getName());
        
        Utils.checkExistance(copied);
        Utils.checkDirectory(destination);
        Utils.checkNonExistance(newDestination);
            
        if (!args[1].equals("-r")) {
            if (copied.isFile()) {
                Utils.copy(copied, newDestination);
            }
            if (copied.isDirectory()) {
                newDestination.mkdir();
            }
        } else {
            recursiveCopy(copied, destination);
        }
    }
    
    public void recursiveCopy(File copied, File destination) throws Exception {
        File newDestination = new File(destination, copied.getName());
        if (copied.isFile()) {
            Utils.copy(copied, newDestination);
        }
        if (copied.isDirectory()) {
            newDestination.mkdir();
            for (File object: copied.listFiles()) {
                recursiveCopy(object, newDestination);
            }
        }
    }
    
    public String name() {
        return "cp";
    }
    
    public int minArgs() {
        return 3;
    }
    
    public int maxArgs() {
        return 4;
    }
}
