package ru.fizteh.fivt.students.vadim_mazaev.shell.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public final class RmCmd {
	private RmCmd() {
		//not called
	}
	
	public static void run(final String[] cmdWithArgs) throws Exception {
		int afterKeyIndex = 1;
		if (cmdWithArgs[1].equals("-r")) {
			afterKeyIndex = 2;
		}
		if (cmdWithArgs.length == 1
				|| (cmdWithArgs.length == 2 && cmdWithArgs[1].equals("-r"))) {
			throw new Exception(getName() + ": missing operand");
		} else if (cmdWithArgs.length > 3
				|| (cmdWithArgs.length > 2 && !cmdWithArgs[1].equals("-r"))) {
			throw new Exception(getName()
						+ ": too much arguments");
		}
		try {
			File removedFile = Paths.get(cmdWithArgs[afterKeyIndex])
							.normalize().toFile();
			if (!removedFile.isAbsolute()) {
				removedFile = Paths.get(System.getProperty("user.dir"),
					cmdWithArgs[afterKeyIndex]).normalize().toFile();
			}
			if (cmdWithArgs[1].isEmpty() || !removedFile.exists()) {
				throw new Exception(getName()
						+ ": cannot remove '"
						+ cmdWithArgs[afterKeyIndex]
						+ "': No such file or directory");
			}
			if (removedFile.isFile()) {
				if (!removedFile.delete()) {
					throw new Exception(getName()
						+ ": cannot remove '"
						+ cmdWithArgs[afterKeyIndex]
						+ "': smth went wrong");
				}
			} else {
				if (afterKeyIndex == 1) {
					throw new Exception(getName()
							+ ": cannot remove '"
							+ cmdWithArgs[afterKeyIndex]
							+ "': is a directory");
				}
				if (!rmRec(removedFile)) {
					throw new Exception(getName()
							+ ": cannot remove '"
							+ cmdWithArgs[afterKeyIndex]
							+ "': smth went wrong");
				}
			}
		} catch (InvalidPathException e) {
			throw new Exception(getName()
					+ ": cannot remove file '"
					+ cmdWithArgs[afterKeyIndex]
					+ "': illegal character in name");
		} catch (SecurityException e) {
			throw new Exception(getName()
					+ ": cannot remove file '"
					+ cmdWithArgs[afterKeyIndex]
					+ "': access denied");
		}
	}
	
	private static boolean rmRec(final File removed) throws IOException {
		if (removed.isDirectory()) {
			for (File f : removed.listFiles()) {
				if (!rmRec(f)) {
					return false;
				}
			}
		}
		return removed.delete();
	}
	
	public static String getName() {
		return "rm";
	}
}
