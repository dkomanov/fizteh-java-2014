package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

import java.io.File;

public class CdCommand extends Command {
	void execute(String[] args) {
		String path = args[1];
		File newDir = (path.charAt(0) == '/') ? new File(path) : new File(
				Shell.curDir, path);
		newDir.mkdirs();
		if (!newDir.exists()) {
			System.out.println("cd: '" + path + "': No such file or directory");
		} else
			Shell.curDir = newDir;
	}

	CdCommand() {
		name = "cd";
	}
}
