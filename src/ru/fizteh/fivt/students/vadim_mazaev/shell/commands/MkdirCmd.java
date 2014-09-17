package ru.fizteh.fivt.students.vadim_mazaev.shell.commands;

import java.io.File;
import java.nio.file.FileSystemException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public final class MkdirCmd {
	private MkdirCmd() {
		//not called
	}
	
	public static void run(final String[] cmdWithArgs) throws Exception {
		if (cmdWithArgs.length == 1) {
			throw new IllegalArgumentException(getName() + ": missing operand");
		} else if (cmdWithArgs.length > 2) {
			throw new IllegalArgumentException(getName()
					+ ": two much arguments");
		}
		try {	
			File makedDir = Paths
				.get(System.getProperty("user.dir"), cmdWithArgs[1]).toFile();
			if (!makedDir.mkdir()) {
				throw new FileSystemException(getName()
						+ ": cannot create directory '"
						+ cmdWithArgs[1] + "': File exists");
			}
		} catch (InvalidPathException e) {
			throw new IllegalArgumentException(getName()
					+ ": cannot create directory '"
					+ cmdWithArgs[1] + "': illegal character in name");
		} catch (SecurityException e) {
			throw new SecurityException(getName()
					+ ": cannot create directory '"
					+ cmdWithArgs[1] + "': access denied");
		}
	}
	
	public static String getName() {
		return "mkdir";
	}
}
