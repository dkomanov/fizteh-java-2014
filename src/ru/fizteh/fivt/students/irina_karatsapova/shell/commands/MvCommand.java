package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

import java.io.File;

public class MvCommand implements Command {
    public void execute(String[] args) throws Exception {
        File moved = Utils.makePathAbsolute(args[1]).toFile();
        File destination = Utils.makePathAbsolute(args[2]).toFile();

        File newDestination;
        if (destination.exists() && destination.isDirectory()) {
            newDestination = new File(destination, moved.getName());
        } else {
            newDestination = destination;
        }

        Utils.checkExistance(moved);
        Utils.checkExistance(newDestination.getParentFile());
        Utils.checkNonExistance(newDestination);
        Utils.checkSubDirectory(moved, newDestination);

        if (moved.isFile()) {
            Utils.copy(moved, newDestination);
            Utils.delete(moved);
        }
        if (moved.isDirectory()) {
            newDestination.mkdir();
            for (File object: moved.listFiles()) {
                recursiveMove(object, newDestination);
            }
            Utils.delete(moved);
        }
    }
    
    public void recursiveMove(File moved, File destination) throws Exception {
        File newDestination = new File(destination, moved.getName());
        if (moved.isFile()) {
            Utils.copy(moved, newDestination);
        }
        if (moved.isDirectory()) {
            newDestination.mkdir();
            for (File object: moved.listFiles()) {
                recursiveMove(object, newDestination);
            }
        }
        Utils.delete(moved);
    }
    
    public String name() {
        return "mv";
    }
    
    public int minArgs() {
        return 3;
    }
    
    public int maxArgs() {
        return 3;
    }
}
