package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;
//package Shell;

import java.io.File;

public class MkdirCommand extends Command {
	boolean execute(String[] args) {
		if (args.length > maxNumberOfArguements) {
			return false;
		}
		String dirName = args[0];
		File newFile = (dirName.charAt(0) == '/') ? new File(dirName)
				: new File(Shell.curDir, dirName);
		if (!newFile.exists()) {
			System.out.println("mkdir: '" + dirName
					+ "': No such file or directory");
			return false;
		}
		newFile.mkdirs();
		return true;
	}

	MkdirCommand() {
		name = "mkdir";
		maxNumberOfArguements = 2;
	}
}
