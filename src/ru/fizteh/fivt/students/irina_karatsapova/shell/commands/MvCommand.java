package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

import java.io.File;
import java.io.IOException;

public class MvCommand implements Command {
	public void execute(String[] args) throws IOException, Exception {
		File copied = Utils.makePathAbsolute(args[1]).toFile();
		File destination = Utils.makePathAbsolute(args[2]).toFile();
		
		Utils.checkExistance(copied);
		
		if (destination.getName().lastIndexOf('.') != -1) { // если это файл 
			copied.renameTo(new File(copied.getParentFile(), destination.getName()));
			destination = destination.getParentFile();
		}
		File newDestination = new File(destination, copied.getName());
		
		Utils.checkExistance(destination);
		Utils.checkNonExistance(newDestination);
		
		recursiveMove(copied, destination);
	}
	
	public void recursiveMove(File moved, File destination) throws IOException, Exception {
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
