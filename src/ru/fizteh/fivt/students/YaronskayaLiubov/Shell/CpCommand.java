package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;
//package Shell;

import java.io.File;

public class CpCommand extends Command {
	boolean execute(String[] args) {
		if (args.length > maxNumberOfArguements) {
			return false;
		}
		int option = (args[1].equals("-r")) ? 1 : 0;
		String source = args[1 + option];
		String destination = args[2 + option];
		if (!source.equals(destination)) {
			File fileSource = (source.charAt(0) == '/') ? new File(source)
					: new File(Shell.curDir, source);
			if (!fileSource.exists()) {
				System.out.println("rm: cannot remove '" + source
						+ "': No such file or directory");
				return false;
			}
			File dirDestination = (destination.charAt(0) == '/') ? new File(
					destination) : new File(Shell.curDir, destination);
			if (!dirDestination.exists()) {
				System.out.println(name + ": cannot remove to '" + destination
						+ "': No such file or directory");
				return false;
			}
			if (option == 0 && fileSource.isDirectory()) {
				System.out.println(name + ": " + source
						+ " is a directory (not copied).");
				return false;
			}
			dirDestination.mkdirs();
			try {
				Shell.dirCopy(fileSource, dirDestination);
			} catch (Exception e) {
				System.out.println(e.toString());
				return false;
			}
		}
		return true;
	}

	CpCommand() {
		name = "cp";
		maxNumberOfArguements = 4;
	}
}
