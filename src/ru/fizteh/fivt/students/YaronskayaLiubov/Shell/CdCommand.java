package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;
//package Shell;

import java.io.File;

public class CdCommand extends Command {
	boolean execute(String[] args) {
		if (args.length > maxNumberOfArguements) {
			return false;
		}
		String path = args[1];
		File newDir = (path.charAt(0) == '/') ? new File(path) : new File(
				Shell.curDir, path);
		newDir.mkdirs();
		if (!newDir.exists()) {
			System.out.println("cd: '" + path + "': No such file or directory");
			return false;
		} else {
			Shell.curDir = newDir;
		}
		return true;
	}

	CdCommand() {
		name = "cd";
		maxNumberOfArguements = 2;
	}
}
