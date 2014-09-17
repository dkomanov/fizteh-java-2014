package ru.fizteh.fivt.students.vadim_mazaev.shell.commands;

import java.io.File;
import java.nio.file.FileSystemException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public final class CdCmd {
	private CdCmd() {
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
			File newWorkingDir = Paths.get(cmdWithArgs[1]).normalize().toFile();
			newWorkingDir = toAbsolute(newWorkingDir);
			if (newWorkingDir.exists()) {
				if (newWorkingDir.isDirectory()) {
					System.setProperty("user.dir", newWorkingDir.getPath());
				} else {
					throw new FileSystemException(getName() + ": '"
						+ cmdWithArgs[1] + "': This is not a directory");
				}
			} else {
				throw new FileSystemException(getName() + ": '"
					+ cmdWithArgs[1] + "': No such file or directory");
			}
		} catch (InvalidPathException e) {
			throw new IllegalArgumentException(getName()
					+ ": cannot change directory to '"
					+ cmdWithArgs[1] + "': illegal character in path");
		} catch (SecurityException e) {
			throw new SecurityException(getName()
					+ ": cannot change directory: access denied");
		}
	}
	
	public static File toAbsolute(final File file) {
		if (!file.isAbsolute()) {
			return Paths.get(System.getProperty("user.dir"), file.getPath())
				.normalize().toFile();
		}
		return file;
	}
	
	public static String getName() {
		return "cd";
	}
}
