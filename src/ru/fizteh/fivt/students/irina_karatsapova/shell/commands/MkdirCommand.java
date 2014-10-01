package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

import java.io.File;

public class MkdirCommand implements Command {
	public void execute(String[] args) throws Exception {
		File newDir = Utils.makePathAbsolute(args[1]).toFile();
		Utils.checkExistance(newDir.getParentFile());
		newDir.mkdir();
	}
	
	public String name() {
		return "mkdir";
	}
	
	public int minArgs() {
		return 2;
	}
	
	public int maxArgs() {
		return 2;
	}
}
