package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

import java.io.File;

public class CpCommand extends Command {
	void execute(String[] args) {
		int option = (args[1].equals("-r")) ? 1:0;
		String source = args[1 + option];
		String destination = args[2 + option];
		if (!source.equals(destination)) {
			File fileSource = (source.charAt(0) == '/') ? new File(source)
					: new File(Shell.curDir, source);
			if (!fileSource.exists()) {
				System.out.println("rm: cannot remove '" + source
						+ "': No such file or directory");
				return;
			}
			File DirDestination = (destination.charAt(0) == '/') ? new File(
					destination) : new File(Shell.curDir, destination);
			if (!DirDestination.exists()) {
				System.out.println(name + ": cannot remove to '" + destination
						+ "': No such file or directory");
				return;
			}
			if (option == 0 && fileSource.isDirectory()) {
				System.out.println(name + ": " + source
						+ " is a directory (not copied).");
				return;
			}
			DirDestination.mkdirs();
			try {
				Shell.dirCopy(fileSource, DirDestination);
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

	CpCommand() {
		name = "cp";
	}
}
