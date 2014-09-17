package ru.fizteh.fivt.students.vadim_mazaev.shell.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class MvCmd {
	private MvCmd() {
		//not called
	}
	
	public static void run(final String[] cmdWithArgs) throws Exception {
		if (cmdWithArgs.length <= 2) {
			throw new Exception(getName() + ": missing operand");
		} else if (cmdWithArgs.length > 3) {
			throw new Exception(getName()
					+ ": two much arguments");
		}
		try {
			File movedFile = Paths.get(cmdWithArgs[1])
							.normalize().toFile();
			if (!movedFile.isAbsolute()) {
				movedFile = Paths.get(System.getProperty("user.dir"),
							cmdWithArgs[1]).normalize().toFile();
			}
			if (!movedFile.exists()) {
				throw new Exception(getName() + ": "
					+ cmdWithArgs[1] + ": No such file or directory");
			}
			File destinationFile = Paths.get(cmdWithArgs[1])
								.normalize().toFile();
			if (!destinationFile.isAbsolute()) {
				destinationFile = Paths.get(System.getProperty("user.dir"),
							cmdWithArgs[2]).normalize().toFile();
			}
			if (movedFile.getParent().equals(destinationFile.getParent())) {
				movedFile.renameTo(destinationFile);
				return;
			}
			if (movedFile.isFile()) {
				if (destinationFile.isDirectory()) {
					destinationFile = Paths.get(destinationFile.getPath(),
								movedFile.getName()).toFile();
				}
				Files.move(movedFile.toPath(), destinationFile.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} else {
				if (!mvRec(movedFile, destinationFile)) {
					throw new Exception(getName() + ": "
						+ "cannot move file '"
						+ cmdWithArgs[1] + "' to '"
						+ cmdWithArgs[2] + "'");
				}
			}
		} catch (IOException e) {
			throw new Exception(getName()
					+ ": cannot move file '"
					+ cmdWithArgs[1] + "' to '"
					+ cmdWithArgs[2] + "'");
		} catch (InvalidPathException e) {
			throw new Exception(getName()
					+ ": cannot move file '"
					+ cmdWithArgs[1] + "' to '"
					+ cmdWithArgs[2] + "': illegal character in name");
		} catch (SecurityException e) {
			throw new Exception(getName()
					+ ": cannot move file '"
					+ cmdWithArgs[1] + "' to '"
					+ cmdWithArgs[2] + "': access denied");
		}
	}
	
	private static boolean mvRec(final File copied, final File destination) {
		if (copied.isDirectory()) {
			destination.mkdir();
			for (File f : copied.listFiles()) {
				mvRec(f, Paths.get(destination.getAbsolutePath(),
						f.getName()).toFile());
			}
			copied.delete();
		} else {
			try {
				Files.move(copied.toPath(), destination.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	
	public static String getName() {
		return "mv";
	}
}
