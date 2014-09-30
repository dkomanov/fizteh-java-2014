package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

import java.io.File;

public class MkdirCommand extends Command {
	void execute(String[] args) {
		String dirName = args[0];
		File newFile = (dirName.charAt(0) == '/') ? new File(dirName)
				: new File(Shell.curDir, dirName);
		if (!newFile.exists()) {
			System.out.println("mkdir: '" + dirName
					+ "': No such file or directory");
		}
		newFile.mkdirs();
	}

	MkdirCommand() {
		name = "mkdir";
	}
}
