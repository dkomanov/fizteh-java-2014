package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

import java.io.File;
import java.io.IOException;

public class CdCommand implements Command {
	public void execute(String[] args) throws IOException, Exception {
		File newDir = Utils.makePathAbsolute(args[1]).toFile();
		
		Utils.checkExistance(newDir);
		Utils.checkDirectory(newDir);
		
		Utils.newCurrentPath(newDir.toString());
	}
	
	public String name() {
		return "cd";
	}
	
	public int minArgs() {
		return 2;
	}
	
	public int maxArgs() {
		return 2;
	}
}
