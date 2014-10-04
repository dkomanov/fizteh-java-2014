package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

import java.io.File;

public class CpCommand implements Command {
    public void execute(String[] args) throws Exception {
        File copied;
        File destination;
        if (!args[1].equals("-r")) {
            Utils.checkArgumentsNumber(args.length, minArgs());
            copied = Utils.makePathAbsolute(args[1]).toFile();
            destination = Utils.makePathAbsolute(args[2]).toFile();
        } else {
            Utils.checkArgumentsNumber(args.length, maxArgs());
            copied = Utils.makePathAbsolute(args[2]).toFile();
            destination = Utils.makePathAbsolute(args[3]).toFile();
            Utils.checkSubDirectory(copied, destination);
        }

        File newDestination;
        if (destination.exists() && destination.isDirectory()) {
            newDestination = new File(destination, copied.getName());
        } else {
            newDestination = destination;
        }
        
        Utils.checkExistance(copied);
        Utils.checkExistance(newDestination.getParentFile());
        Utils.checkNonExistance(newDestination);

        if (copied.isFile()) {
            Utils.copy(copied, newDestination);
        }
        if (copied.isDirectory()) {
            newDestination.mkdir();
            if (args[1].equals("-r")) {
                for (File object: copied.listFiles()) {
                    recursiveCopy(object, newDestination);
                }
            }
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
